
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;

public class Frame extends JFrame {
	private JTextArea txtArea = new JTextArea(10,30);
	private JPanel panel = new JPanel();
	private JButton saveB = new JButton("Save");
	private JButton parseB = new JButton("Parse");
	private JButton dualB = new JButton("Dual");
	private int MinMax = 0 ; // -1 = min 1 = max 
	private String linearProblem = "";
	private String objectiveFunction = "";
	private String periorismoi = "";
	private int[] cT = new int[5];
	private int[][] A = new int[5][5];
	private int[] Eqin = new int[5]; // -1 : <= , 0 : = , 1 : >= 
	private int[] b = new int[5];
	private int[] wT = new int[5];
	private int[][] wA = new int[5][5];
	private int[] wb = new int[5];
	private int[] wEqin = new int[5]; // -1 : <= , 0 : = , 1 : >=
	
	
	public Frame(){
		super("Parcer");
		saveB.addActionListener(new SaveBListener());
		parseB.addActionListener(new ParseBListener());
		dualB.addActionListener(new DualBListener());
		
		panel.add(txtArea);
		panel.add(saveB);
		panel.add(parseB);
		panel.add(dualB);
		
		this.setContentPane(panel);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void checkSyntax(){
		boolean flag = true;
		boolean foundST = false;
		String minORmax = linearProblem.substring(0,3);
		int i=0;
		int j=1;
		
		if(minORmax.equals("min") || minORmax.equals("max")){
			try{
				while(flag){
					if(linearProblem.substring(i,j+2).equals("end")){
						flag = false;
					}
					else if(linearProblem.substring(i, j).equals("s") || linearProblem.substring(i, j).equals("S")){
						if(linearProblem.substring(i+1, j+1).equals("t") || linearProblem.substring(i+1, j+1).equals("T")){
							foundST = true;
						}
					}
					i++;
					j++;
				}
			} catch(StringIndexOutOfBoundsException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Your problem MUST have an end.\nPlease try again!!","ERROR",JOptionPane.ERROR_MESSAGE);
				linearProblem = "";
			}
		}
		else{
			JOptionPane.showMessageDialog(null,"Objective function MUST be min or max.\nPlease try again!!","ERROR",JOptionPane.ERROR_MESSAGE);
			linearProblem = "";
			foundST = true;
		}
		
		if(!foundST){
			JOptionPane.showMessageDialog(null,"You MUST put st or ST before your periorismoi start.\nPlease try again!!","ERROR",JOptionPane.ERROR_MESSAGE);
			linearProblem = "";
		}
		
		
	}
	
	private void findMinOrMax(){
		if(linearProblem.substring(0, 3).equals("min")){
			MinMax = -1;
			linearProblem = linearProblem.substring(3);
		}
		else if(linearProblem.substring(0, 3).equals("max")){
			MinMax = 1;
			linearProblem = linearProblem.substring(3);
		}
	}
	
	private void findObjectiveFunction(){
		int i = 0;
		int j = 1;
		boolean flag = true;
		String temp = "";
		int cols = 0;
		
		while(flag){
			if(linearProblem.substring(i, j).equals("S") || linearProblem.substring(i, j).equals("s")){
				if(linearProblem.substring(i+1, j+1).equals("T") || linearProblem.substring(i+1, j+1).equals("t")){
					objectiveFunction = linearProblem.substring(0,j+1);
					periorismoi = linearProblem.substring(j+1,linearProblem.length());
					flag = false ;
				}
				
			}
			i++;
			j++;
		}
		
		flag = true;
		
		while(flag){
			if(objectiveFunction.substring(0,1).equals("S") || objectiveFunction.substring(0,1).equals("s")){
				flag = false;
			}
			if(objectiveFunction.substring(0,1).equals("+")){
				objectiveFunction = objectiveFunction.substring(1);
				
				while(!objectiveFunction.substring(0,1).equals("x")){
					temp = temp.concat(objectiveFunction.substring(0,1));
					objectiveFunction = objectiveFunction.substring(1);
				}
				
				cT[cols] = Integer.parseInt(temp);
				temp="";
				cols++;
			}
			
			else if(objectiveFunction.substring(0,1).equals("-")){
				temp = temp.concat(objectiveFunction.substring(0,1));
				objectiveFunction = objectiveFunction.substring(1);
				
				while(!objectiveFunction.substring(0,1).equals("x")){
					temp = temp.concat(objectiveFunction.substring(0,1));
					objectiveFunction = objectiveFunction.substring(1);
				}
				
				cT[cols] = Integer.parseInt(temp);
				temp="";
				cols++;
			}
			
			else{
				objectiveFunction = objectiveFunction.substring(1);
			}
			
		}
		
		
	}
	
	
	private void findAEqinb(){
		boolean flag = true;
		boolean flag2 = true;
		String temp = "";
		int rows= 0;
		int cols = 0;
		
		for(int i=0;i<Eqin.length;i++){
			Eqin[i] = 2;
		}
		
		while(flag){
			if(periorismoi.substring(0, 3).equals("end")){
				flag  = false;
			}
			else if(periorismoi.substring(0, 1).equals("\n")){
				periorismoi = periorismoi.substring(1);
				rows++;
				cols = 0;
			}
			
			else if(periorismoi.substring(0,1).equals("+")){
				periorismoi = periorismoi.substring(1);
				while(!periorismoi.substring(0,1).equals("x")){
					temp = temp.concat(periorismoi.substring(0,1));
					periorismoi = periorismoi.substring(1);
				}
				periorismoi = periorismoi.substring(2);
				A[rows][cols] = Integer.parseInt(temp);
				temp = "";
				cols++;
			}
			
			else if(periorismoi.substring(0,1).equals("-")){
				temp = periorismoi.substring(0,1);
				periorismoi = periorismoi.substring(1);
				while(!periorismoi.substring(0,1).equals("x")){
					temp = temp.concat(periorismoi.substring(0,1));
					periorismoi = periorismoi.substring(1);
				}
				periorismoi = periorismoi.substring(2);
				A[rows][cols] = Integer.parseInt(temp);
				temp = "";
				cols++;
				
			}
			
			else if(periorismoi.substring(0,1).equals("=")){
				Eqin[rows] = 0;
				periorismoi = periorismoi.substring(1);
				
				while(flag2){
					if(periorismoi.substring(0,1).equals("\n")){
						flag2 = false;
					}
					else if(periorismoi.substring(0,3).equals("end")){
						flag2 = false;
					}
					else{
						temp = temp.concat(periorismoi.substring(0,1));
						periorismoi= periorismoi.substring(1);
					}
				}
				b[rows] = Integer.parseInt(temp);
				temp = "";
				flag2 = true;
			}
			
			else if(periorismoi.substring(0, 1).equals("<")){
				Eqin[rows] = -1;
				periorismoi = periorismoi.substring(2);
				
				while(flag2){
					if(periorismoi.substring(0,1).equals("\n")){
						flag2 = false;
					}
					else if(periorismoi.substring(0,3).equals("end")){
						flag2 = false;
					}
					else{
						temp = temp.concat(periorismoi.substring(0,1));
						periorismoi= periorismoi.substring(1);
					}
				}
				b[rows] = Integer.parseInt(temp);
				temp = "";
				flag2 = true;
			}
			
			else if(periorismoi.substring(0, 1).equals(">")){
				Eqin[rows] = 1;
				periorismoi = periorismoi.substring(2);
				
				while(flag2){
					if(periorismoi.substring(0,1).equals("\n")){
						flag2 = false;
					}
					else if(periorismoi.substring(0,3).equals("end")){
						flag2 = false;
					}
					else{
						temp = temp.concat(periorismoi.substring(0,1));
						periorismoi= periorismoi.substring(1);
					}
				}
				b[rows] = Integer.parseInt(temp);
				temp = "";
				flag2 = true;
			}
			
			else{
				periorismoi =periorismoi.substring(1);
			}
			
		}
		
		
	}
	
	private void saveSolution(){
		String solution ="";
		
		solution = solution.concat("MinMax = "+MinMax);
		solution = solution.concat("\ncT:\n");
		for(int it:cT){
			solution = solution.concat(it+"\t");
		}
		
		solution = solution.concat("\nA:\t\t\t\t\tEqin:\tb:\n");
		for(int i=0;i<5;i++){
			if(Eqin[i] != 2){
				for(int j=0;j<5;j++){
					solution = solution.concat(A[i][j]+"\t");
				}
				solution = solution.concat(""+Eqin[i]+"\t"+b[i]);
				solution = solution.concat("\n");
			}
			
		}
		System.out.println(solution);
		
		try{
			File finalF = new File("final.txt");
			FileWriter filewriter = new FileWriter(finalF);
			
			filewriter.write(solution);
			filewriter.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	
	private void saveDualSolution(){
		String dualSolution ="";
		
		dualSolution = dualSolution.concat("MinMax = "+MinMax*-1);
		dualSolution = dualSolution.concat("\nwT:\n");
		for(int it:wT){
			dualSolution = dualSolution.concat(it+"\t");
		}
		
		dualSolution = dualSolution.concat("\nA:\t\t\t\t\tEqin:\tb:\n");
		for(int i=0;i<5;i++){
			if(wEqin[i] != 2){
				for(int j=0;j<5;j++){
					dualSolution = dualSolution.concat(wA[i][j]+"\t");
				}
				dualSolution = dualSolution.concat(""+wEqin[i]+"\t"+wb[i]);
				dualSolution = dualSolution.concat("\n");
			}
		}
		
		System.out.println(dualSolution);
		
		for(int i=0;i<5;i++){
			int index = i+1;
			
			if(Eqin[i] == -1){
				dualSolution = dualSolution.concat("w"+index+">=0, ");
				System.out.print("w"+index+">=0, ");
			}
			else if(Eqin[i] == 1){
				dualSolution = dualSolution.concat("w"+index+"<=0, ");
				System.out.print("w"+index+"<=0, ");
			}
			else if(Eqin[i] == 0){
				dualSolution = dualSolution.concat("w"+index+" free, ");
				System.out.print("w"+index+" free, ");
			}
		}
		
		
		try{
			File finalF = new File("dual.txt");
			FileWriter filewriter = new FileWriter(finalF);
			
			filewriter.write(dualSolution);
			filewriter.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	
	class SaveBListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String content = txtArea.getText(); 
			byte data[] = content.getBytes();
			
			try {
				FileOutputStream os = new FileOutputStream("initial.txt");
				os.write(data);
				os.close();
				//txtArea.setText(content2);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
			
		
	}

	class ParseBListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				FileInputStream is = new FileInputStream("initial.txt");
				int count =0;
				char c;
				
				while((count = is.read()) != -1){
					c = (char) count;
					if(c != ' '){
						linearProblem = linearProblem.concat(Character.toString(c));
					}
				}
				is.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			checkSyntax();
			findMinOrMax();
			findObjectiveFunction();
			findAEqinb();
			saveSolution();
			
			
		}
		
	}

	class DualBListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			int i;
			int j;
			
			for(i=0;i<5;i++){
				wT[i] = b[i];
				wb[i] = cT[i];
				if(Eqin[i] != 2){
					wEqin[i] = 1;
				}
				else{
					wEqin[i] = 2;
				}
				
				for(j=0;j<5;j++){
					wA[i][j] = A[j][i];
				}
			}
			
			saveDualSolution();
			
			
			
		}
		
	}
}
