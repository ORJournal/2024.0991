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
import java.util.List;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author lozanolo
 */
public class Arc {

    int tail;
    int head;
    int arcVal;
    int varIndex;
    double costCoeff;
    double[][] uVector;
    String key;
    List<Integer> vars;

    /**
     * Constructor by parameters <br>
     *
     * @param _index Variable associated with arc. <br>
     * @param value Value of the arc. <br>
     * @param _tail Tail of the arc. <br>
     * @param _head Head of the arc. <br>
     */
    public Arc(int _index, int value, int _tail, int _head) {
        tail = _tail;
        head = _head;
        arcVal = value;
        varIndex = _index;
        costCoeff = 0;
        //uVector = new double[k]; Probably we don't even have to store this... 
        if (_index >= 0) {
            key = "NO arc for variable z_" + varIndex;
        } else {
            key = "Artificial arc";
        }
    }

    Arc(int _index, int value, int _tail, int _head, double[] u) {
        key = "";
        tail = _tail;
        head = _head;
        arcVal = value;
        varIndex = _index;
        uVector = new double[1][u.length];
        System.arraycopy(u, 0, uVector[0], 0, u.length);
        costCoeff = Math.pow(AlgHandler.a.dotProduct(new ArrayRealVector(u)), 2);
        key = "YES arc for variable z_" + varIndex + ", uVector: [ ";
        for (int i = 0; i < uVector.length; i++) {
            key = key + uVector[0][i] + " ";
        }
        key += "], Cost: " + costCoeff;
    }



    public void print() {

        System.out.println(key);

    }

}
