/**
 *  Copyright 2014 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 *  Copyright 2014 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.europeana.querylog.cli.learn;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europeana.querylog.learn.Evaluate;
import eu.europeana.querylog.learn.measure.Measure;
import eu.europeana.querylog.learn.measure.MeasureFactory;
import eu.europeana.querylog.learn.query.BM25FSolrResults;

/**
 * This CLI performs the learning of the bm25f parameters by using an
 * {@link Evaluator}. In order to execute the cli you will have to run: <br/>
 * <br/>
 * <code>
 * 		java eu.europeana.querylog.cli.learn.LearnBM25FParametersCLI -goldentruth dir -measure [NDCG@24|P@24|R@24] -log logfile
 * </code> <br/>
 * <br/>
 * where:
 * 
 * <ul>
 * <li> <code>goldentruth</code> a directory containing one or several
 * assessments</li>
 * <li> <code>measure</code> the quality measure to optimize (NDCG, precision
 * (P), recall (R)</li>
 * <li> <code>log</code> a log file where the best parameters found will be
 * written.
 * </ul>
 * 
 * 
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 1, 2014
 */
public class LearnBM25FParametersCLI extends AbstractCommandLineInterface {

	private static String[] params = new String[] { "goldentruth", "measure",
			"log" };

	private static final Logger logger = LoggerFactory
			.getLogger(LearnBM25FParametersCLI.class);
	private static ProjectProperties properties = new ProjectProperties(
			LearnBM25FParametersCLI.class);

	private static String usage = "java -cp $jar  eu.europeana.querylog.cli.learn.LearnBM25FParametersCLI -goldentruth assessment-dir -measure measure-to-optimize -log log-file";

	public LearnBM25FParametersCLI(String[] args) {
		super(args, params, usage);
	}

	public static void main(String[] args) {
		LearnBM25FParametersCLI cli = new LearnBM25FParametersCLI(args);
		File assessmentFolder = new File(cli.getParam("goldentruth"));
		String[] fields = properties.get("bm25f.fields").split(",");
		Measure m = MeasureFactory.getMeasure(cli.getParam("measure"));
		logger.info("optimizing {} ", m.getName());
		Evaluate evaluate = new Evaluate(assessmentFolder,
				Arrays.asList(fields), m, new BM25FSolrResults());
		evaluate.setLog(cli.getParam("log"));
		String snippet = evaluate.learningToRank();
		logger.info("learning finished, copy and paste the final values in solrconfig");
		System.out.println(snippet);

	}
}
