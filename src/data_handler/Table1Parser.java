/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data_handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author user
 */
public class Table1Parser {
    
    /**
     * Reads raw output file and outputs Table 1 from the paper. <br>
     * @param args: path to raw output file, path to table file 
     */
    public static void main(String[] args) throws IOException {
        writeSolution(args[1], readSolution(args[0]));
    }
    
    
    
     /**
     * Reads the data from file. <br>
     *
     * @param path Path to file
     */
    public static Map<String,double[]> readSolution(String path) {
       
        BufferedReader br = null;
        String cvsSplitBy = ",";
        String line;
        String[] row, value;
        Map<String,double[]> table= new HashMap<>();
        boolean solved;

        try {
            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                row = line.split(cvsSplitBy);
                int tau=Integer.parseInt(row[5].trim());
                int n = Integer.parseInt(row[1].trim());
                String name = tau+" "+n;
                double[] values=table.get(name);
                if(values==null)
                    values = new double[10];
                if(Integer.parseInt(row[7].trim())==0) // Mosek
                {
                    values[0]+=1; // Number of instances solved with mosek observed with this key
                    values[1]+=Double.parseDouble(row[12].trim()); // Time used by Mosek
                    values[2]+=Double.parseDouble(row[17].trim()); // Nodes explored by Mosek
                    values[3]+=(Double.parseDouble(row[18].trim())<1e-4?1:0); // Number of instances solved to optimality
                }
                else // DD
                {
                    values[4]+=1; // Number of instances solved with DD observed with this key
                    solved=Double.parseDouble(row[9].trim())>0;
                    if(solved)
                    {
                        values[5]+=Double.parseDouble(row[9].trim()); // Number of arcs in the DD
                        values[6]+=Double.parseDouble(row[10].trim()); // Time to construct the DD
                        values[7]+=Double.parseDouble(row[11].trim()); // Time to solve the shortest path in the DD
                        values[8]+=1; // Number of instances solved to optimality
                        values[9]+=Double.parseDouble(row[12].trim()); // Time to solve the SOCP relaxation
//                    System.out.println(name+"\t"+values[6]+ " "+values[8]);
                    }
                    else
                    {
                        values[6]+=1800;
                    }
                }
                table.put(name, values);
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return table;
    }
    
    /**
     * Writes the table to a CVS file. <br>
     *
     * @param path Path to file
     * @param table Table with the information
     */
    public static void writeSolution(String path, Map<String,double[]> table) throws IOException {
       
        try (FileWriter out = new FileWriter(new File(path), false)) {
//            out.write(F.length+","+F[0].length+"\n");
            out.write("tau, n, time_msk, nodes_msk, perc_msk, arcs_dd, time_dd, time_sp, perc_dd, time_socp\n");
            for (Map.Entry<String, double[]> entry : table.entrySet()) {
                
                String[] key = entry.getKey().split(" ");
                
                double[] val = entry.getValue();
                out.write(key[0]+","+key[1]+",");
                for (int i = 1; i <= 3; i++) {
                    out.write(val[i]/val[0]+",");
                }
                for (int i = 5; i <= 9; i++) {
                    if( i==8 || i==6)
                    {
                        out.write(val[i]/val[4]+",");
                    }
                    else
                    {
                        out.write(val[i]/val[8]+(i<9?",":""));
                    }
                    
                }
                out.write("\n");
                
            }

        }
    }
    
}
