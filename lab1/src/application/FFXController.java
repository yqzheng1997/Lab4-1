package application;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FFXController {
  private final Desktop desktop = Desktop.getDesktop();
  private String textToFile = new String("");
  public static final int MAX = 10000;
  public static HashMap<String, PointInf> graph;
  public static String rand_point;
  public static String randStart;
  public static String rand_pair;
  public static String rand_show;
  public static boolean rand_finish;
  public static ArrayList<String> rand_pool;

  class PointInf {
    ArrayList<Integer> weight = new ArrayList<Integer>();
    ArrayList<String> adj = new ArrayList<String>();
  }

  @FXML
  private Button showgraphb;
  @FXML
  private Button seekbrigewordb;
  @FXML
  private Button createnewtextb;
  @FXML
  private Button seekshortestpathb;
  @FXML
  private Button randomwalkb;
  @FXML
  private Button spbutton;
  @FXML
  private Button ctbutton;
  @FXML
  private Button rwnext;
  @FXML
  private Button rwend;
  @FXML
  private Button bwbutton;
  @FXML
  private TextField spword1;
  @FXML
  private TextField spword2;
  @FXML
  private TextField bwword1;
  @FXML
  private TextField bwword2;
  @FXML
  private TextField bwresult;
  @FXML
  private TextArea spresult;
  @FXML
  private TextArea oldText;
  @FXML
  private TextArea newText;
  @FXML
  private TextArea rwresult;


  @FXML
  protected void readText(ActionEvent event) {
    Stage stage1 = new Stage();
    openText(stage1);
  }

  @FXML
  protected void showDirectedGraph(ActionEvent event) { // draw the directed graph by graphviz
    GraphViz gv = new GraphViz();
    String adj;
    ArrayList<String> points;
    gv.addln(gv.start_graph());
    for (String value : graph.keySet()) {
      points = graph.get(value).adj;
      for (String des : points) {
        adj = value + "->" + des 
          + "[label=\"" + graph.get(value).weight.get(points.indexOf(des)) + "\"]";
        gv.addln(adj);
      }
    }
    gv.addln(gv.end_graph());
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String fileName = "D:\\" + dateFormat.format(date) + ".jpg";
    File file = new File(fileName);
    gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), "jpg"), file);
    try {
      Desktop.getDesktop().open(new File(fileName));// open the graph
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  @FXML
  protected void seekBrigeWord(ActionEvent event) {
    Stage stage3 = new Stage();
    seekBrige(stage3);
  }

  @FXML
  protected void createNewText(ActionEvent event) {
    Stage stage4 = new Stage();
    createText(stage4);
  }

  @FXML
  protected void seekShortestPath(ActionEvent event) {
    Stage stage5 = new Stage();
    findPath(stage5);
  }
   
  @FXML
  protected void randWalk(ActionEvent event) {
    Stage stage6 = new Stage();
    randWalk(stage6);
  }
  
  @FXML
  void subCreateNewText(ActionEvent event) {
    String input = oldText.getText();
    newText.setText("the new text is as follows:" + generateNewText(input));
  }
  
  @FXML
  void setBrigeWord(ActionEvent event) {
    String ret = queryBridgeWords(bwword1.getText(), bwword2.getText());
    if (ret.equals("")) {
      bwresult.setText("No word1 or word2 in the graph!");
    } else if (ret.equals(" ")) {
      bwresult.setText("No bridge words from word1 to word2!");
    } else {
      String out = "";
      String[] strList = ret.split(" ");
      if (strList.length == 1) {
        out = out + strList[0];
      } else {
        for (int index = 0; index < strList.length; index++) {
          // form the format
          if (index == strList.length - 1) {
            out = out + " and " + strList[index] + ".";
          } else if (index == strList.length - 2) {
            out = out + strList[index];
          } else {
            out = out + strList[index] + ',';
          }
        }
      }
      bwresult.setText("The bridge words from word1 to word2 are:" + out + ".");
    }
  }
  
  @FXML
  protected void shortestPath(ActionEvent event) {
    spresult.setText(calcShortestPath(spword1.getText(), spword2.getText()));
  }
  
  @FXML
  protected void storePath(ActionEvent event) throws IOException {
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String fileName = "D:\\" + dateFormat.format(date) + ".txt";
    File file = new File(fileName);
    file.createNewFile();
    BufferedWriter output = new BufferedWriter(new FileWriter(file));
    output.write(textToFile);
    output.close();
    Stage stage = new Stage();
    Label label = new Label("文件保存成功，路径为:" + fileName);
    Pane pane = new Pane();
    pane.getChildren().add(label);
    Scene myScene = new Scene(pane);
    stage.setScene(myScene);
    stage.show();
  }
  
  
 
  void findPath(Stage stage) {
    stage.setTitle("查找最短路径");
    Pane myPane = null;
    try {
      myPane = (Pane) FXMLLoader.load(getClass().getResource("seekShortestPath.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scene myScene = new Scene(myPane);
    stage.setScene(myScene);
    stage.show();
  }
  
  /**
  * This is first line.
  */ 
  
  public void buildGraph(String[] strArray) { // function producing the graph
    HashMap<String, PointInf> tgraph = new HashMap<String, PointInf>(); // global variable
    PointInf value;
    int index;
    Integer w;
    for (int i = 0; i < strArray.length; i++) {
      if (strArray[i].equals("")) {
        continue;
      } else {
        value = tgraph.get(strArray[i]);
        if (value == null) {
          value = new PointInf();  
        }
          
        for (int j = i + 1; j < strArray.length; j++) {
          if (!strArray[j].equals("")) {
            index = value.adj.indexOf(strArray[j]);
            if (index == -1) {
              value.adj.add(strArray[j]);
              value.weight.add(value.adj.indexOf(strArray[j]), 1);
            } else {
              w = value.weight.get(index) + 1;
              value.weight.add(index, w);
            }
            break;
          }            
        }
          
        tgraph.put(strArray[i], value);
      }
    }
    this.setGraph(tgraph);
    
  }

  void openText(Stage stage) {
    stage.setTitle("选择文件");

    final FileChooser fileChooser = new FileChooser();

    Button openButton = new Button("打开文件");
    Label openLabel = new Label("...");
    openLabel.setWrapText(true);
    openLabel.setTranslateX(200);
    openButton.setOnAction((final ActionEvent e) -> {
      File file = fileChooser.showOpenDialog(stage);
      if (file != null) {
        openLabel.setText("文件打开成功");// if the file has been opened successfully,
        // the other buttons will be visible
        showgraphb.setVisible(true);
        seekbrigewordb.setVisible(true);
        createnewtextb.setVisible(true);
        seekshortestpathb.setVisible(true);
        randomwalkb.setVisible(true);
        char[] buffer;
        Reader input = null;
        try {
          input = new FileReader(file);
          buffer = new char[(int) file.length()]; // text buffer
          input.read(buffer);
          String[] strArray = new String(buffer).toLowerCase().split("[^a-zA-Z]"); 
          // split the original text
          input.close();
          buildGraph(strArray);
        } catch (IOException e2) {
          e2.printStackTrace();
        }
      } else {
        openLabel.setText("文件打开失败");
        // when failed to open the text,the other buttons won't be visible
        showgraphb.setVisible(false);
        seekbrigewordb.setVisible(false);
        createnewtextb.setVisible(false);
        seekshortestpathb.setVisible(false);
        randomwalkb.setVisible(false);
      }
    });

    GridPane inputGridPane = new GridPane();

    GridPane.setConstraints(openButton, 0, 0);
    inputGridPane.setHgap(6);
    inputGridPane.setVgap(6);
    inputGridPane.getChildren().addAll(openLabel);
    inputGridPane.getChildren().addAll(openButton);

    FlowPane rootGroup = new FlowPane();
    rootGroup.getChildren().addAll(inputGridPane);
    rootGroup.setPadding(new Insets(12, 12, 12, 12));

    stage.setScene(new Scene(rootGroup));
    stage.show();
  }

  void openFile(File file) {
    EventQueue.invokeLater(() -> {
      try {
        desktop.open(file);
      } catch (IOException ex) {
        Logger.getLogger(FFXController.class.getName()).log(Level.SEVERE, null, ex);
      }
    });
  }

  String queryBridgeWords(String word1, String word2) {
    // query the bridge words
    if (!(graph.containsKey(word1) && graph.containsKey(word2))) {
      return "";  
    }
    String ret = "";
    PointInf value;
    PointInf bridge;
    value = graph.get(word1);
    for (String ele : value.adj) {
      bridge = graph.get(ele);
      for (String des : bridge.adj) {
        if (des.equalsIgnoreCase(word2)) {
          ret = ret.equals("") ? ele : ret + " " + ele;
        }
      }
    }
    if (ret.equals("")) {
      return " ";
    }
    return ret;
  }

  

  void seekBrige(Stage stage) {
    stage.setTitle("查找桥接词");
    Pane myPane = null;
    try {
      myPane = (Pane) FXMLLoader.load(getClass().getResource("seekBrigeWordPage.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scene myScene = new Scene(myPane);
    stage.setScene(myScene);
    stage.show();
  }

  /**
   * This is first line.
   */ 
   

  public String generateNewText(String inputText) { 
    // produce new text according to the bridge word
    String searchResult;
    String ret;
    String[] resultList;
    String[] inputList = inputText.split(" ");
    ret = inputList[0];
    for (int index = 1; index < inputList.length; index++) {
      searchResult = queryBridgeWords(inputList[index - 1], inputList[index]);
      if (searchResult.equals("") || searchResult.equals(" ")) {
        ret = ret + " " + inputList[index];
        continue;
      }
      resultList = searchResult.split(" ");
      Random rand = new Random();
      int i = rand.nextInt(resultList.length);
      ret = ret + " " + resultList[i] + " " + inputList[index];
    }
    return ret;
  }

  void createText(Stage stage) {
    stage.setTitle("生成新文本");
    Pane myPane = new Pane();
    try {
      myPane = (Pane) FXMLLoader.load(getClass().getResource("createNewText.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scene myScene = new Scene(myPane);
    stage.setScene(myScene);
    stage.show();
  }
  
  /**
   * This is first line.
   */ 
   
  @SuppressWarnings("unchecked")
    public String calcShortestPath(String word1, String word2) { 
    // calculate the shortest path between two words

    if (word2.equals("")) {
      if (!(graph.containsKey(word1))) {
        return "The word is not exist!";
      }
    } else if (!(graph.containsKey(word1) && graph.containsKey(word2))) {
      return "The word1 or word2 is not exist!";
    }
    String ret = "";
    int len = 0;// boolean find = false;
    PointInf bridge;
    ArrayList<String> pass;
    LinkedList<String> queue = new LinkedList<String>();
    queue.addFirst(word1);
    HashMap<String, Boolean> visit = new HashMap<String, Boolean>();
    HashMap<String, ArrayList<String>> path = new HashMap<String, ArrayList<String>>(); 
    // mark the path
    HashMap<String, Integer> dis = new HashMap<String, Integer>(); 
    // djikstral,initialize the distance array
    for (String iter : graph.keySet()) {
      len++;
      if (iter.equals(word1)) {
        dis.put(iter, 0);
      } else {
        dis.put(iter, 10000);
      }
      visit.put(iter, false);
    }
    String temp;
    ArrayList<String> crossPoint = new ArrayList<String>();
    crossPoint.add(word1);
    path.put(word1, crossPoint);
    for (int i = 0; i < len; i++) {
      temp = "";
      for (String iter : graph.keySet()) {
        if (!visit.get(iter) && (temp.equals("") || dis.get(temp) > dis.get(iter))) {
          temp = iter;
        }  
      }           
      visit.put(temp, true);
      bridge = graph.get(temp);
      for (String ele : bridge.adj) {
        if (dis.get(ele) > dis.get(temp) + bridge.weight.get(bridge.adj.indexOf(ele))) {
          dis.put(ele, dis.get(temp) + bridge.weight.get(bridge.adj.indexOf(ele)));
          pass = new ArrayList<String>();
          pass.add(temp);
          path.put(ele, pass);
        } else if (dis.get(ele) == dis.get(temp) + 1) {
          pass = path.get(ele);
          pass.add(temp);
        }
      }
    }
    String onePath;
    String[] onePathList;
    int count;
    Stack<Integer> flag = new Stack<Integer>();
    int arrayInd;
    Stack<ArrayList<String>> stack = new Stack<ArrayList<String>>();
    HashMap<String, ArrayList<String>> copyPath = new HashMap<String, ArrayList<String>>();
    if (word2.equals("")) {
      for (String iter : graph.keySet()) {
        if (iter.equals(word1)) {
          continue;
        }
        ret = ret + "\r\nfind the path between " + word1 + " and " + iter + ": \r\n";
        copyPath = (HashMap<String, ArrayList<String>>) path.clone();
        if (dis.get(iter) == MAX) {
          ret = ret + "there is no path between two words!\r\n";
        } else {
          stack.clear();
          stack.push(copyPath.get(iter));
          flag.push(1);
          onePath = iter;
          while (!stack.isEmpty()) { // use the date structure stack;
            crossPoint = stack.peek();
            onePath = onePath + " " + crossPoint.get(flag.peek() - 1);
            if (crossPoint.equals(copyPath.get(word1))) {
              onePathList = onePath.split(" "); // add a new path to ret
              ret = ret + "Path is :\r\n" + onePathList[onePathList.length - 1];
              for (int ind = onePathList.length - 2; ind >= 0; ind--) {
                ret = ret + "->" + onePathList[ind];
              }
              ret = ret + ". Length is:" + dis.get(iter) + ".\r\n";
              count = 0;
              onePath = iter;
              while (crossPoint.size() == flag.peek()) {
                stack.pop();
                flag.pop();
                count++;
                if (!stack.isEmpty()) {
                  crossPoint = stack.peek();
                } else {
                  crossPoint = null;
                  break;
                }
              }
              if (crossPoint != null) {
                for (int i = 1; i <= onePathList.length - count - 2; i++) {
                  onePath = onePath + " " + onePathList[i];  
                }
                arrayInd = flag.pop();
                arrayInd++;
                flag.push(arrayInd);
                onePath = onePath + " " + crossPoint.get(flag.peek() - 1);
              }
            } else {
              stack.push(copyPath.get(crossPoint.get(flag.peek() - 1)));
              flag.push(1);
            }
          }
        }
      }
    } else { // the shortest path between two words
      if (dis.get(word2) == MAX) {
        return "there is no path between two words!";   
      }       
      copyPath = (HashMap<String, ArrayList<String>>) path.clone();
      stack.push(copyPath.get(word2));
      onePath = word2;
      while (!stack.isEmpty()) { // use the date structure stack;
        crossPoint = stack.peek();
        onePath = onePath + " " + crossPoint.get(0);
        if (crossPoint.equals(copyPath.get(word1))) {
          onePathList = onePath.split(" "); // add a new path to ret
          ret = ret + "\r\nPath is :\r\n" + onePathList[onePathList.length - 1];
          for (int ind = onePathList.length - 2; ind >= 0; ind--) {
            ret = ret + "->" + onePathList[ind];
          }
          ret = ret + ". Length is:" + dis.get(word2) + ".";
          count = 0;
          onePath = word2;
          while (crossPoint.size() == 1) {
            stack.pop();
            count++;
            if (!stack.isEmpty()) {
              crossPoint = stack.peek();
            } else {
              crossPoint = null;
              break;
            }
          }
          if (crossPoint != null) {
            crossPoint.remove(0);
            for (int i = 1; i <= onePathList.length - count - 2; i++) {
              onePath = onePath + " " + onePathList[i];
            }    
            stack.push(crossPoint);
          }
        } else {
          stack.push(copyPath.get(crossPoint.get(0)));
        }
      }
    }
    return ret;
  }

  @FXML
  protected void nextStep(ActionEvent event) {
    String node = randomWalk();
    rwresult.appendText(node);
    if (!(node.equals("\r\nthe path is over!") || node.equals("\r\nthe path already exists!"))) {
      textToFile = textToFile.concat(node);
    }
  }

  /**
   * This is first line.
   */ 
   

  

  public String randomWalk() {
    if (rand_finish) {
      return "";   
    }     
    String ret = "";
    if (randStart.equals("")) {
      Random rand = new Random();
      int finish = rand.nextInt(graph.size());
      int count = 1;
      for (String tmp : graph.keySet()) {
        if (count++ == finish) {
          rand_point  = tmp;
          this.setrandStart(tmp);
          break;
        }
      }
      ret = randStart + " ";
    } else {
      if (graph.get(randStart).adj.isEmpty()) {
        rand_finish = true;
        return "\r\nthe path is over!";
      }
      for (String value : graph.get(randStart).adj) {
        ret = ret.equals("") ? value : ret + " " + value;
      }
      String[] strList = ret.split(" ");
      int cnt = 0;
      Random rand = new Random();
      int choice;
      while (true) {
        choice = rand.nextInt(strList.length - cnt);
        rand_pair = randStart + "->" + strList[choice];
        if (rand_pool.isEmpty() || rand_pool.indexOf(rand_pair) == -1) {
          rand_pool.add(rand_pair);
          randStart = ret = strList[choice];
          ret += " ";
          break;
        } else {
          ret = "\r\nthe path already exists!";
          rand_finish = true;
          break;
        }
      }
    }
    return ret;
  }

  public static HashMap<String, PointInf> getGraph() {
    return graph;
  }

  public static void setGraph(HashMap<String, PointInf> graph) {
    FFXController.graph = graph;
  }

  public static String getrandStart() {
    return randStart;
  }

  public static void setrandStart(String randStart) {
    FFXController.randStart = randStart;
  }

  void randWalk(Stage stage) {
    stage.setTitle("随机游走");
    rand_pool = new ArrayList<String>();
    rand_pair = randStart = rand_point = "";
    rand_finish = false;
    Pane myPane = null;
    try {
      myPane = (Pane) FXMLLoader.load(getClass().getResource("randomWalk.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scene myScene = new Scene(myPane);
    stage.setScene(myScene);
    stage.show();
  }

}
