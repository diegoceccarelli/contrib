package pt.utl.ist.repox.task;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.task.TaskFactory.TaskType;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class TaskFileHelper {

	public static List<Task> loadTasks(File tasksFile)
			throws DocumentException, ClassNotFoundException, NoSuchMethodException, ParseException {
		List<Task> loadedTasks = new ArrayList<Task>();
		
		if(!tasksFile.exists()) {
			return loadedTasks;
		}
		
		Document document = new SAXReader().read(tasksFile);
		List<Element> taskElements = document.getRootElement().elements("task");
		if(!taskElements.isEmpty()) {
			for (Element loadedTaskElement : taskElements) {
				TaskType taskType = TaskFactory.TaskType.valueOf(loadedTaskElement.attributeValue("type"));
				Task task = TaskFactory.getInstance(taskType);
				task.getXml(loadedTaskElement);
				loadedTasks.add(task);
			}
		}
		
		return loadedTasks;
	}
	
	public static synchronized void saveTasks(File tasksFile, List<Task> tasks) throws IOException {
		Document document = DocumentHelper.createDocument();
		Element rootNode = document.addElement("tasks");
		
		if(tasks != null) {
			for (Task currentTask : tasks) {
				Element taskNode = rootNode.addElement("task");
				currentTask.setXml(taskNode);
			}
		}
		
		XmlUtil.writePrettyPrint(tasksFile, document);
	}	
	

	public static synchronized void saveSingleTask(File tasksFile, Task task)
			throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, ParseException {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);
		saveTasks(tasksFile, tasks);
	}
	
	public static synchronized void saveTask(File tasksFile, Task task)
			throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, ParseException {
		List<Task> loadedTasks = loadTasks(tasksFile);
		loadedTasks.add(task);
		saveTasks(tasksFile, loadedTasks);
	}
	
	/**
	 * Removes task from Tasks file if there is an equivalent action (same taskClass and parameters) in the file.  
	 * 
	 * @param task
	 */
	public static synchronized void removeTask(File tasksFile, Task task)
			throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, ParseException {
		List<Task> loadedTasks = loadTasks(tasksFile);
		
		Iterator<Task> iter = loadedTasks.iterator();
		while (iter.hasNext()) {
			Task runningTask = iter.next();
			if(runningTask.equalsAction(task)) {
				task.setFinishTime(Calendar.getInstance());
				iter.remove();
			}
		}
		
		TaskFileHelper.saveTasks(tasksFile, loadedTasks);
	}
	
	
}