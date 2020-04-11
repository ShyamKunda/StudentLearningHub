package inquerro.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {


    private String fileNameToSearch;
    private String resultpath = null;
    private List<String> result = new ArrayList<String>();

    public String getFileNameToSearch() {
        return fileNameToSearch;
    }

    public void setFileNameToSearch(String fileNameToSearch) {
        this.fileNameToSearch = fileNameToSearch;
    }

    public List<String> getResult() {
        return result;
    }

//    public static void main(String[] args) {
//
//        FileSearch fileSearch = new FileSearch();
//
//        //try different directory and filename :)
//
//
//        System.out.println( fileSearch.searchDirectory(new File(  System.getProperty("user.dir")), "serviceAccount.json"));
//
//        int count = fileSearch.getResult().size();
//        if(count ==0){
//            System.out.println("\nNo result found!");
//        }else{
//            System.out.println("\nFound " + count + " result!\n");
//            for (String matched : fileSearch.getResult()){
//                System.out.println("Found : " + matched);
//            }
//        }
//    }

    public String searchDirectory(File directory, String fileNameToSearch) {

        setFileNameToSearch(fileNameToSearch);

        if (directory.isDirectory()) {
            String result = search(directory);

            return resultpath;
        } else {
            System.out.println(directory.getAbsoluteFile() + " is not a directory!");
        }
        return null;

    }

    private String  search(File file) {

        if (file.isDirectory()) {
            System.out.println("Searching directory ... " + file.getAbsoluteFile());

            //do you have permission to read this directory?
            if (file.canRead()) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        search(temp);
                    } else {
                        System.out.println("-----FileName " + temp.getName().toLowerCase() + "==" + getFileNameToSearch().toLowerCase() );
                        if (getFileNameToSearch().toLowerCase().equals(temp.getName().toLowerCase())) {
                            result.add(temp.getAbsoluteFile().toString());
                            resultpath =temp.getAbsoluteFile().toString();
                            return temp.getAbsoluteFile().toString();
                        }

                    }
                }

            } else {
                System.out.println(file.getAbsoluteFile() + "Permission Denied");
            }
        }
        return null;


    }

}
