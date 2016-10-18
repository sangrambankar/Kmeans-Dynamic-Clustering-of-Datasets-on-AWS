

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * Servlet implementation class TitanicServlet
 */
@WebServlet("/TitanicServlet")
public class TitanicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger("TitanicServlet");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TitanicServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	    ServletContext context = getServletContext( );
		
		String input_file = null;
		PrintWriter disp=response.getWriter();
		String column1 = null,column2 = null;
		JsonObject json = new JsonObject();
	    JsonArray toplevel = new JsonArray();
	    JsonObject sublevel;
		
		try {
			List<FileItem> fields = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			String clusters = "2";
			// Fetching the values from the jsp file
			for (FileItem field : fields) {
				if (field.isFormField()) {
					String fieldName = field.getFieldName();
					String fieldValue = field.getString();
					if(fieldName.equals("clusters")){
						clusters=fieldValue;
					}else if(fieldName.equals("column1")){
						column1 = (fieldValue);
					}else if(fieldName.equals("column2")){
						column2 = (fieldValue);
					}
					
				} else {
					input_file = FilenameUtils.getName(field.getName());
					InputStream file_content = field.getInputStream();
					File get_file = new File(input_file);
					FileUtils.copyInputStreamToFile(file_content,get_file);
				}
			}
			
			context.log(input_file);

			SimpleKMeans simplekmeans = new SimpleKMeans();
			simplekmeans.setSeed(10);
			simplekmeans.setPreserveInstancesOrder(true);
			simplekmeans.setNumClusters(Integer.parseInt(clusters));
			
			
			
			ArrayList<Double> col1=new ArrayList<Double>();
			ArrayList<Double> col2=new ArrayList<Double>();
			
			
			Instances data_instances = generateInstances(input_file);

			//####n for deleting a instances
			
			/*for (int i = data_instances.numInstances() - 1; i >= 0; i--) {
			    Instance inst = data_instances.instance(i);
			    if (inst.value(getNumber(column1)) == 0) {
			    	data_instances.delete(i);
			    }
			}*/
			
			//Fetching the data
			for(int i=0;i<data_instances.numInstances();i++)
			{
				col1.add(data_instances.instance(i).value(getNumber(column1)));
				col2.add(data_instances.instance(i).value(getNumber(column2)));
			}
			
			simplekmeans.buildClusterer(data_instances);
			
			context.log(""+simplekmeans.getNumClusters());
			
			HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();
			

			
			JsonArray json_cent=new JsonArray();
			Instances centroids = simplekmeans.getClusterCentroids();
			for (int i = 0; i < centroids.numInstances(); i++) {
				
				JsonObject cluster_data=new JsonObject();
				cluster_data.addProperty("ccol1", centroids.instance(i).value(getNumber(column1)));
				cluster_data.addProperty("ccol2", centroids.instance(i).value(getNumber(column1)));
				cluster_data.addProperty("cclusters", (i+1));
				json_cent.add(cluster_data);	 
				context.log("Cluster" + i+1 + ": "+ centroids.instance(i));
				
			}
			
			
			// This array returns the cluster number (starting with 0) for each instance. The array has as many elements as the number of instances
			int[] values = simplekmeans.getAssignments();
			JsonArray json_arr=new JsonArray();
			int i=0;
			for(int totclusters : values) {
				
			/*	int num  = hmap.get(totclusters);
				if(num != null && num != 0){
					num += 1;
				}else{
					num = 1;
				}
				hmap.put(totclusters, num);*/
				
				JsonObject cluster_data=new JsonObject();
				cluster_data.addProperty("col1", col1.get(i));
				cluster_data.addProperty("col2", col2.get(i));
				cluster_data.addProperty("totclusters", totclusters);
				json_arr.add(cluster_data);	   	
				i++;
			}

			System.out.println(json_arr);
			request.setAttribute("jcluster", json_arr);
			request.setAttribute("clusters", clusters);
			request.setAttribute("col1name", "'"+column1+"'");
			request.setAttribute("col2name", "'"+column2+"'");
			request.setAttribute("jcentroid", json_cent);

			

			
			RequestDispatcher dispatch=request.getRequestDispatcher("response.jsp");
			dispatch.forward(request, response);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	private static int getNumber(String name){
		int num = -1;
		switch (name) {
		case "age":
			num = 4;
			break;
			
		case "survived":
			num = 1;
			break;

		default:
			break;
		}
		
		return num;
	}
	
	private Instances generateInstances(String filename) throws IOException{
		
		String arffname = "";
		//Getting the data from CSV file 
		CSVLoader load_file = new CSVLoader();
		load_file.setSource(new File(filename));
		Instances instan = load_file.getDataSet();
		String[] records=filename.split("\\.");
		ArffSaver arff_saver = new ArffSaver();
		arff_saver.setInstances(instan);
		File f = new File(records[1]+".arff");
		arff_saver.setFile(f);
		//arff_saver.setDestination(new File(records[0]+".arff"));
		arff_saver.writeBatch();
		BufferedReader buff_file = readDataFile(records[1]+".arff"); 
		Instances data_instance = new Instances(buff_file);
		
		
		
		return data_instance;
	}

	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}

		return inputReader;
	}
}
