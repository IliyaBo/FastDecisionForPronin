/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author DZharinov
 */
public class MainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        HashMap<String, String> map = readFile("C:\\Users\\DZharinov\\Desktop\\123.txt");
        ArrayList<String> list = readAllFile("C:\\Users\\DZharinov\\Desktop\\output.txt");
        map.remove(map.remove("**.*"));
        System.out.println("*** File extension and wildcard matching ***");
        String[] extensions = {"java", "class"};
        String wildcardMatcher = "*2850-95*.*";
        Path dirPath = Paths.get("\\\\yupronin\\WORKS\\ФЦЦС\\Материалы\\");
        System.out.println("Directory being searched: " + dirPath.toString());
        //map.put("*5781-016-00*.*", "777");
        recursionSearch(dirPath, map, false);
        changeList(list, map);

        FileOutputStream fos = new FileOutputStream("C:\\Users\\DZharinov\\Desktop\\newOutput.txt");

        StringBuilder allStrings = new StringBuilder();
        for (String str : list) {
            allStrings.append(str);
        }
        byte[] buffer = allStrings.toString().getBytes();
        fos.write(buffer, 0, buffer.length);

        /*for (String str : allStrings.toString().split("\n")) {
            System.out.println(str);
        }*/
    }

    private static void changeList(ArrayList<String> list, HashMap<String, String> map) {
        go:
        for (int i = 0; i < list.size(); i++) {
            //System.out.println("str = " + list.get(i));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                //System.out.println("key = " + key);
                if (list.get(i).contains(key.replaceAll("\\*|\\.", ""))) {
                    //System.out.println("str="+str+", "+allStrings.indexOf(str));
                    //System.out.println("str = " + list.get(i) + ", key = " + key);
                    String newValue = value + "\t" + list.get(i);
                    list.set(i, newValue);
                    continue go;
                }
            }
        }
    }

    private static HashMap readFile(String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF8"));
        String line = null;
        HashMap<String, String> map = new HashMap();
        String ls = "\n";

        try {
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                if (line.replace("\r\n", "").isEmpty()) {
                    continue;
                }
                map.put("*" + line.trim() + "*.*", ""); //
                //stringBuilder.append(line);
                //stringBuilder.append(ls);
            }

            return map;
        } finally {
            reader.close();
        }
    }

    private static ArrayList readAllFile(String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF8"));
        String line = null;
        ArrayList<String> list = new ArrayList();
        String ls = "\n";

        try {
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                list.add(line);
                list.add("\n");

                //stringBuilder.append(line);
                //stringBuilder.append(ls);
            }

            return list;
        } finally {
            reader.close();
        }
    }

    private static void recursionSearch(Path dirPath, HashMap<String, String> map, boolean fileFound) {
        /*if (fileFound) {
            System.out.println("return");
            return;
        }*/
        //System.out.println("searcg in path =" + dirPath);
        try (DirectoryStream dirStream = Files.newDirectoryStream(dirPath)) {
            List<Path> innerDirPath = new ArrayList<>();
            for (Iterator it = dirStream.iterator(); it.hasNext();) {
                Path filePath = (Path) it.next();
                String filename = filePath.getFileName().toString();
                if (filePath.toUri().toString().endsWith("/")) {
                    innerDirPath.add(filePath);
                }
                //System.out.println(filename);
                // file extension matching
                /*fileFound = FilenameUtils.isExtension(filename, extensions);
                if (fileFound) {
                    System.out.println("    file with java or class extension");
                }*/
                // wild card matching
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!value.isEmpty()) {
                        continue;
                    }
                    fileFound = FilenameUtils.wildcardMatch(filename, key);
                    if (fileFound) {
                        //System.out.println("file found = "+key+", in path =" + filePath);
                        entry.setValue(filePath.getParent().getFileName() + "\t" + filePath.getParent().getParent());
                        break;
                    }
                }

                /*if (fileFound) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if(!value.isEmpty()){
                        System.out.println(entry);
                        }
                    }
                }*/
            } // end for loop
            //System.out.println(innerDirPath);
            if (!fileFound) {
                for (Path path : innerDirPath) {
                    MainClass.recursionSearch(path, map, fileFound);
                }
            }
        } catch (Exception ex) {
            System.out.println("catch! ex=" + ex.toString());
        }

    }
}
