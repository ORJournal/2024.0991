/*

MIT License

Copyright (c) 2025 Andres Gomez

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

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
public class TableOnlineParser {

    /**
     * Reads raw output file and outputs Table 1 from the paper. <br>
     *
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
    public static Map<String, double[]> readSolution(String path) {

        BufferedReader br = null;
        String cvsSplitBy = ",";
        String line;
        String[] row, value;
        Map<String, double[]> table = new HashMap<>();

        boolean solved;

        try {
            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                row = line.split(cvsSplitBy);
                int k = Integer.parseInt(row[4].trim());
                double lambda = Double.parseDouble(row[3].trim());
                int n = Integer.parseInt(row[1].trim());
                String name = k + " " + lambda + " " + n;
                double[] values = table.get(name);
                if (values == null) {
                    values = new double[5];
                }

                values[0] += 1; // Number of instances observed this key
                values[1] += Double.parseDouble(row[10].trim()); // Number of arcs in the DD
                values[2] += Double.parseDouble(row[11].trim()); // Time to construct the DD
                values[4] += Double.parseDouble(row[12].trim()); // Total time to solve the shortest path in the DD
                values[3] += Double.parseDouble(row[12].trim())/((double)n);
               
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
    public static void writeSolution(String path, Map<String, double[]> table) throws IOException {

        try (FileWriter out = new FileWriter(new File(path), false)) {
//            out.write(F.length+","+F[0].length+"\n");
            out.write("k, lambda, arcs_dd, time_dd, time_sp, time_total\n");
            for (Map.Entry<String, double[]> entry : table.entrySet()) {

                String[] key = entry.getKey().split(" ");
                out.write(key[0] + "," + key[1] + ",");

                double[] val = entry.getValue();
                for (int i = 1; i <= 4; i++) {
                    out.write(val[i] / val[0] +  (i < 4 ? "," : ""));
                }
                
                out.write("\n");

            }

        }
    }

}
