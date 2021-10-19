import java.util.ArrayList;

public class Printer {

	public ArrayList<String> fileNames;
	public String printerName;
	
	public Printer(String name, int sizeOfQueue)
	{
		fileNames = new ArrayList<String>(sizeOfQueue);
		printerName = name;
	}
	
	public void addFile(String filename)
	{
		fileNames.add(filename);
	}
	
	public void moveFirstInQueue(int jobId)
	{
		String fileName = fileNames.get(jobId);
	    fileNames.remove(jobId);
	    fileNames.add(0, fileName);

	}
	public void deleteFile(String fileName)
	{
		int itemPos = fileNames.indexOf(fileName);
		if(itemPos != 0)
		{
			fileNames.remove(itemPos);
		}	
	}
	public void listQueue()
	{
		for(int i = 0; i < fileNames.size(); i++)
		{
			System.out.println(fileNames.get(i));
		}
		
	}
	
	
	
	public static void main(String[] args) {
		//Helper Functions Tests
		//createPrinter();
		//Printer("Printer1",50);
		/*addFile("File1");
		addFile("File2");
		addFile("File3");
		addFile("File4");
		addFile("File5");
		for(String element : fileNames)
		{
			System.out.println(element);
		}
		System.out.println(" ");
		System.out.println("Move First in Queue index 3");
		System.out.println(" ");
		moveFirstInQueue(3);
		for(String element : fileNames)
		{
			System.out.println(element);
		}
		System.out.println(" ");
		System.out.println("Delete File2");
		System.out.println(" ");
		deleteFile("File2");
		for(String element : fileNames)
		{
			System.out.println(element);
		}
	
		*/
		// TODO Auto-generated method stub

	}

}
