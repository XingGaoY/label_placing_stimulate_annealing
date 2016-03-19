import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

class node{
    double x;
    double y;
    node(){}
    node(double _x, double _y){
        x = _x;
        y = _y;
    }
}

class conflict_n{
    int conflict_node_num;
    int[][] conflict_label_position;
    conflict_n(int num, int[][] p){
        conflict_node_num = num;
        conflict_label_position = p;
    }
}

class conflict_arr{
    int[][] arr1;
    int[][] arr2;
}

public class label_placing {
    private node[] readin(String fileName, int num)throws IOException {        //readin from file
        File file = new File(fileName);
        BufferedReader reader;
        String line;
        node[] n = new node[num];
        try{
            System.out.println("File is being read in.");
            reader = new BufferedReader(new FileReader(file));
            int linenum=0;
            double[] temp = new double[2];

            while((((line = reader.readLine())) != null) & (linenum < num)){
                if(linenum%10==0)
                    System.out.println(linenum+"nodes has been read in.");
                line = line.replaceAll("\\s+","|");
                String[] arr = line.split("\\|");
                for(int j=0,k=0;j<arr.length;j++,k++){
                    if(arr[j].length()<=0){
                        k-=1;
                        continue;
                    }
                    temp[k] = Double.valueOf(arr[j]);
                }
                n[linenum] = new node(temp[0],temp[1]);
                linenum++;
            }
            System.out.println("All nodes read in..");
        }catch (IOException e){
            System.err.println("Error found when read in."+ e);
            e.printStackTrace();
        }
        return n;
    }

    private conflict_arr node_lable_conflict(node n1, node n2, node label){
        conflict_arr ans = null;
        int[][] arr1 = new int[4][4];
        int[][] arr2 = new int[4][4];
        boolean swap = false;
        double d_x = n2.x - n1.x;
        double d_y = n2.y - n2.y;
        if(d_x < 0){
            swap = true;
            d_x = -d_x;
        }
        d_x /= label.x;
        d_y /= label.y;
        switch ((int) Math.ceil(d_x + 0.001)){
            case 1 : switch((int) Math.ceil(d_y + 0.001)){
                case 2 :
                    arr1[0][0] = 3; arr1[0][1] = 4; arr1[1][0] = 3;
                    arr2[2][0] = 1; arr2[2][1] = 2; arr2[3][0] = 1;
                    break;
                case 1 :
                    arr1[0][0] = 1; arr1[0][1] = 2; arr1[0][2] = 3; arr1[0][3] = 4; arr1[1][0] = 2; arr1[1][1] = 3; arr1[2][0] = 3; arr1[3][0] = 3; arr1[3][1] = 4;
                    arr2[0][0] = 1; arr2[1][0] = 1; arr2[1][1] = 2; arr2[2][0] = 1; arr2[2][1] = 2; arr2[2][2] = 3; arr2[2][3] = 4; arr2[3][0] = 1; arr2[3][1] = 4;
                    break;
                case 0 :
                    arr1[0][0] = 1; arr1[0][1] = 2; arr1[1][0] = 2; arr1[2][0] = 2; arr1[2][1] = 3; arr1[3][0] = 1; arr1[3][1] = 2; arr1[3][2] = 3; arr1[3][3] = 4;
                    arr2[0][0] = 1; arr2[0][1] = 4; arr2[1][0] = 1; arr2[1][1] = 2; arr2[1][2] = 3; arr2[1][3] = 4; arr2[2][0] = 3; arr2[2][1] = 4; arr2[3][0] = 4;
                    break;
                case -1 :
                    arr1[2][0] = 2; arr1[3][0] = 1; arr1[3][1] = 2;
                    arr2[0][0] = 4; arr2[1][0] = 3; arr2[1][1] = 4;
                    break;
            }
                break;
            case 2 : switch((int) Math.ceil(d_y + 0.001)){
                case 2 :
                    arr1[0][0] = 3;
                    arr2[2][0] = 1;
                    break;
                case 1 :
                    arr1[0][0] = 2; arr1[0][1] = 3; arr1[3][0] = 3;
                    arr2[1][0] = 1; arr2[2][0] = 1; arr2[2][1] = 4;
                    break;
                case 0 :
                    arr1[0][0] = 2; arr1[3][0] = 2; arr1[3][1] = 3;
                    arr2[1][0] = 1; arr2[1][1] = 4; arr2[2][0] = 4;
                    break;
                case -1 :
                    arr1[3][0] = 2;
                    arr2[1][0] = 4;
                    break;
            }
                break;
        }
        if(swap == true)
        {
            ans.arr1 = arr2; ans.arr2 = arr2;
        }
        else {
            ans.arr1 = arr1; ans.arr2 = arr2;
        }
        return ans;
    }

    private Vector<conflict_n>[] conflict_graph_process(node[] nodes, node label_size){
        Vector<conflict_n>[] ans = new Vector[nodes.length];
        for(int i = 0; i < nodes.length; i++){
            for(int j = i + 1; j < nodes.length; j++){
                double d_x = Math.abs(nodes[i].x - nodes[j].x);
                double d_y = Math.abs(nodes[i].y - nodes[j].y);
                if(d_x >= 2*label_size.x || d_y >= 2*label_size.y) continue;
                conflict_arr conflict_bt = node_lable_conflict(nodes[i], nodes[j], label_size);
                ans[i].add(new conflict_n(j, conflict_bt.arr1));
                ans[j].add(new conflict_n(i, conflict_bt.arr2));
            }
        }
        return ans;
    }

    private int[] SA(Vector<conflict_n>[] conflict_graph, node label_size){
        int[] position = new int[conflict_graph.length];
        int num = conflict_graph.length;
        for(int i = 0; i < num; i++){
            position[i] = (int)(Math.random() * 4);
        }

        double T = 1 / Math.log(1.5);

        for(int i = 0; i < 50; i++){
            int count = 0;
            for(int j = 0; j < 20 * num; j++){
                int node_ch = (int)(Math.random() * num);
                int new_po = (int)(1 + Math.random() * 3) + position[node_ch];
                int delta_E = 0;
                int E_new = 0, E_old = 0;
                for(conflict_n anele : conflict_graph[node_ch]){
                    for(int la = 0; la < 4; la++) {
                        int conflict_po = anele.conflict_label_position[new_po][la];
                        if (conflict_po == 0) break;
                        if(conflict_po == position[anele.conflict_node_num])E_new++;
                    }
                }
                for(conflict_n anele : conflict_graph[node_ch]){
                    for(int la = 0; la < 4; la++) {
                        int conflict_po = anele.conflict_label_position[position[node_ch]][la];
                        if (conflict_po == 0) break;
                        if(conflict_po == position[anele.conflict_node_num])E_old++;
                    }
                }
                delta_E = E_new - E_old;
                if(delta_E < 0){
                    count ++;
                    position[node_ch] = new_po;
                }
                else{
                    double probability_to_re = Math.exp(delta_E / T);
                    double probability_generated = Math.random();
                    if(probability_generated < probability_to_re){
                        count++;
                        position[node_ch] = new_po;
                    }
                }
                if(count == 5 * num)break;
            }
            if(count == 0) break;
            T *= 0.9;
        }

        return position;
    }

    public static void main(String[] arg) throws IOException{
        label_placing lp = new label_placing();
        int num;
        String filename;
        node[] coor;
        node label_size;

        Scanner rd = new Scanner(System.in);
        System.out.println("Please enter the filename.");
        filename = rd.nextLine();
        System.out.println("Please enter the number to be labelled");
        num = rd.nextInt();
        System.out.println("Please enter the size of the label.");
        double x = rd.nextDouble();
        double y = rd.nextDouble();
        label_size = new node(x,y);

        coor = lp.readin(filename, num);
        Vector<conflict_n>[] conflict_graph;
        conflict_graph = lp.conflict_graph_process(coor, label_size);

        int[] label_placing_ans = lp.SA(conflict_graph, label_size);
    }
}
