// GraphViz.java - a simple API to call dot from Java programs
/*$Id$*/
/*
 ******************************************************************************
 *                                                                            *
 *              (c) Copyright 2003 Laszlo Szathmary                           *
 *                                                                            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published by   *
 * the Free Software Foundation; either version 2.1 of the License, or        *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public    *
 * License for more details.                                                  *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program; if not, write to the Free Software Foundation,    *
 * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.                              *
 *                                                                            *
 ******************************************************************************
 */

package application;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
/**
 * <dl>
 * <dt>Purpose: GraphViz Java API
 * <dd>
 *
 * <dt>Description:
 * <dd> With this Java class you can simply call dot
 *      from your Java programs
 * <dt>Example usage:
 * <dd>
 * <pre>
 *    GraphViz gvvvvv = new GraphViz();
 *    gvvvvv.addln(gvvvvv.startgraph());
 *    gvvvvv.addln("A -> B;");
 *    gvvvvv.addln("A -> C;");
 *    gvvvvv.addln(gvvvvv.endgraph());
 *    System.out.println(gvvvvv.getDotSource());
 *
 *    String type = "gif";
 *    File out = new File("out." + type);   // out.gif in this example
 *    gvvvvv.writeGraphToFile( gvvvvv.getGraph( gvvvvv.getDotSource(), type ), out );
 * </pre>
 * </dd>
 *
 * </dl>
 *
 * @version v0.3, 2010/11/29 (November) -- Windows support + ability
 * @version v0.2, 2010/07/22 (July) -- bug fix
 * @version v0.1, 2003/12/04 (December) -- first release
 */

public class GraphViz {
  /**
     * The dir. where temporary files will be created.
     */
  //private static String TEMP_DIR = "/tmp"; // Linux
  private static String TEMP_DIR = "D:/lab1test"; // Windows
  /**
     * Where is your dot program located? It will be called externally.
     */
  // private static String DOT = "/usr/bin/dot"; // Linux
  private static String DOT = "D:/Program Files (x86)/Graphviz/bin/dot.exe"; // Windows
  /**
     * The source of the graph written in dot language.
     */
  private StringBuilder graph = new StringBuilder();
  /**
     * Constructor: creates a new GraphViz object that will contain
     * a graph.
     */
    
  public GraphViz() {
  }
  /**
     * Returns the graph's source description in dot language.
     * @return Source of the graph in dot language.
   */
  
  public String getDotSource() {
    return graph.toString();
  }
  /**
     * Adds a string to the graph's source (without newline).
   */
  
  public void add(String line) {
    graph.append(line);
  }
  /**
     * Adds a string to the graph's source (with newline).
   */
  
  public void addln(String line) {
    graph.append(line + "\n");
  }
  /**
     * Adds a newline to the graph's source.
   */
  
  public void addln() {
    graph.append('\n');
  }
  /**
     * Returns the graph as an image in binary format.
     * @return A byte array containing the image of the graph.
   */
  
  public byte[] getGraph(String dotSource, String type) {
    File dot;
    byte[] imgStream = null;

    try {
      dot = writeDotSourceToFile(dotSource);
      if (dot != null) {
        imgStream = get_img_stream(dot, type);
        if (!dot.delete()) {
          System.err.println("Warning: " + dot.getAbsolutePath() + " could not be deleted!");
        }     
        return imgStream;
      }
      return null;
    } catch (java.io.IOException ioe) { 
      return null; 
    }
  }
  /**
     * Writes the graph's image in a file.
     * @param img   A byte array containing the image of the graph.
     * @param file  Name of the file to where we want to write.
     * @return Success: 1, Failure: -1
   */
    
  public int writeGraphToFile(byte[] img, String file) {
    File to = new File(file);
    return writeGraphToFile(img, to);
  }
  /**
     * Writes the graph's image in a file.
     * @param img   A byte array containing the image of the graph.
     * @param to    A File object to where we want to write.
     * @return Success: 1, Failure: -1
   */
    
  public int writeGraphToFile(byte[] img, File to) {
    try {
      FileOutputStream fos = new FileOutputStream(to);
      fos.write(img);
      fos.close();
    } catch (java.io.IOException ioe) { 
      ioe.printStackTrace();
      return -1; 
    }
    return 1;
  }
  /**
     * It will call the external dot program, and return the image in
     * binary format.
     * @param dot Source of the graph (in dot language).
     * @return The image of the graph in .gif format.
   */
    
  private byte[] get_img_stream(File dot, String type) {
    File img;
    byte[] imgStream = null;
    try {
      img = File.createTempFile("graph_", "." + type, new File(GraphViz.TEMP_DIR));
      Runtime rt = Runtime.getRuntime();

      // patch by Mike Chenault
      String[] args = {DOT, "-T" + type, dot.getAbsolutePath(), "-o", img.getAbsolutePath()};
      Process p = rt.exec(args);

      p.waitFor();
      FileInputStream in = new FileInputStream(img.getAbsolutePath());
      imgStream = new byte[in.available()];
      in.read(imgStream);
      // Close it if we need to
      if (in != null) {
        in.close();
      }
      if (!img.delete()) {
        System.err.println("Warning: " + img.getAbsolutePath() + " could not be deleted!"); 
      }               
    } catch (java.io.IOException ioe) {
      System.err.println("Error:in I/O processing of tempfile in dir " + GraphViz.TEMP_DIR + "\n");
      System.err.println("       or in calling external command");
      ioe.printStackTrace();
    } catch (java.lang.InterruptedException ie) {
      System.err.println("Error: the execution of the external program was interrupted");
      ie.printStackTrace();
    }
    return imgStream;   
  }
  /**
     * Writes the source of the graph in a file, and returns the written file
     * as a File object.
     * @param str Source of the graph (in dot language).
     * @return The file (as a File object) that contains the source of the graph.
   */
    
  public File writeDotSourceToFile(String str) throws java.io.IOException {
    File temp;
    try {
      temp = File.createTempFile("graph_", ".dot.tmp", new File(GraphViz.TEMP_DIR));
      FileWriter fout = new FileWriter(temp);
      fout.write(str);
      fout.close();
    } catch (Exception e) {
      System.err.println("Error: I/O error while writing the dot source to temp file!");
      return null;
    }
    return temp;
  }
  /**
     * Returns a string that is used to start a graph.
     * @return A string to open a graph.
   */
    
  public String startgraph() {
    return "digraph G {" ;
  }
  /**
     * Returns a string that is used to end a graph.
     * @return A string to close a graph.
   */
    
  public String endgraph() {
    return "}";
  }
  /**
     * Read a DOT graph from a text file.
     *
   */
    
  public void readSource(String input) {
    StringBuilder sb = new StringBuilder();

    try {
      FileInputStream fis = new FileInputStream(input);
      DataInputStream dis = new DataInputStream(fis);
      BufferedReader br = new BufferedReader(new InputStreamReader(dis));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      dis.close();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }

    this.graph = sb;
  }

} // end of class GraphViz