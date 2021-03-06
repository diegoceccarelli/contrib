package pt.utl.ist.repox.z3950;

import org.apache.log4j.Logger;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.iface.SearchException;
import org.jzkit.search.provider.iface.Searchable;
import org.jzkit.search.provider.z3950.SimpleZAuthenticationMethod;
import org.jzkit.search.provider.z3950.Z3950ServiceFactory;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetException;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pt.utl.ist.marc.Record;

public abstract class AbstractHarvester implements HarvestMethod {
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(AbstractHarvester.class);

    protected ApplicationContext appContext;
    protected Z3950ServiceFactory factory;
    protected Searchable currentSearchable;
    protected Target target;
    protected int queryRetries = 3;

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    protected AbstractHarvester(Target target) {
        this.target = target;
        appContext = new ClassPathXmlApplicationContext( "ZHarvesterApplicationContext.xml" );
        if ( appContext == null )
            throw new RuntimeException("Unable to locate TestApplicationContext.xml definition file");
    }

    protected void connect() {
        factory = new Z3950ServiceFactory(target.getAddress(), target.getPort());
        factory.setApplicationContext(appContext);
        factory.setDefaultRecordSyntax(target.getRecordSyntax());
        factory.setDefaultElementSetName("F");
//	        factory.getRecordArchetypes().put("Default","sutrs:Resource:F");
        if(target.getUser() != null && !target.getUser().equals("")) {
            SimpleZAuthenticationMethod auth_method = new SimpleZAuthenticationMethod(3, target.getUser(), null, target.getPassword());
            factory.setAuthMethod(auth_method);
        }
    }

    protected void close() {
        if(currentSearchable!=null) {
            try {
                currentSearchable.close();
            } catch (Exception e) {
                log.debug(e.getMessage(),e);
                e.printStackTrace();
            }
        }
    }

    protected IRResultSet runQuery(String queryString) {
        log.info(queryString);
        int tries=0;
        while (tries < queryRetries){
            tries++;

            IRQuery query = new IRQuery();
            query.collections.add(target.getDatabase());
            query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString(queryString);

            IRResultSet result;
            try {
                if(currentSearchable==null) {
                    currentSearchable = factory.newSearchable();
                    currentSearchable.setApplicationContext(appContext);
                }
                result = currentSearchable.evaluate(query);

                result.waitForStatus(IRResultSetStatus.COMPLETE | IRResultSetStatus.FAILURE, 30000);

//	    		log.info(result.getFragmentCount());
            } catch (IRResultSetException ex) {
                log.info(ex.getMessage(),ex);
                continue;
            } catch (SearchException ex) {
                log.info(ex.getMessage(),ex);
                currentSearchable = null;
                continue;
            }
            if(result!=null && result.getStatus()!=IRResultSetStatus.FAILURE)
                return result;

            else if(result!=null && result.getStatus()==IRResultSetStatus.FAILURE)
                log.info("Search failure: "+result.getResultSetInfo());
            else
                log.info("Search failure - uknown error");
        }
        return null;
    }

    protected Record handleRecord(InformationFragment frag) {
        if(frag != null){
            byte[] originalObject = (byte[]) frag.getOriginalObject();
            return new Record(originalObject, target.getCharacterEncoding().toString());
        }
        else{
            System.out.println("frag = " + frag);
            return null;
        }
    }

    public void init() {
        connect();
    }

    public void cleanup() {
        close();
    }
}
