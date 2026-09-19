/**
 * MIT License

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

 */
package minlpdd_ConsecutiveOnes;

import java.util.ArrayList;

/**
 *
 * @author lozanolo
 */
public class DecisionDiagram {
    
    int sourceId;
    int sinkId;

    ArrayList<NodeLight> nodes;  //Nodes in the DD
    ArrayList<Arc> arcs;    //Arcs in the DD
    int lastNode; //Points to the index of the last node in the nodes array, source node is always 0
    

    public DecisionDiagram() {
        nodes = new ArrayList<>();
        arcs = new ArrayList<>();
    }

    public void print() {

        System.out.println("**********************************************DD*****************************************************************");
        for (int i = 0; i < nodes.size(); i++) {
                System.out.println("Node" + nodes.get(i).ID );
                for (int j = 0; j < nodes.get(i).out.size(); j++) {
                    System.out.println("   "+nodes.get(i).out.get(j).key+", "+nodes.get(i).out.get(j).tail+" --> "+nodes.get(i).out.get(j).head);
                }
        }
    }

    public void clearInOut() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).in.clear();
            nodes.get(i).out.clear();
        }

    }

    void print2() {
       System.out.println("Number of Nodes: "+nodes.size());
       System.out.println("Number of Arcs: "+arcs.size());
    }

}
