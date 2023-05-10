import javax.swing.*; //graphic elements
import java.awt.*;
import java.awt.event.*; //allows graphic elements to work
import java.util.Random; //generate random integer
import java.io.*; //file

/**
*EasyKeno -- Creates a GUI for the betting game Keno.
*@author Hazelle Limos
*/
public class EasyKeno extends JFrame{
   //instance variables
   private final int WIDTH = 1000; //window width
   private final int HEIGHT = 800; //window height
   private final int SIZE = 40; //numbers button array size
   
   private JPanel numbers = new JPanel();
   private JPanel northPanel = new JPanel();
   private JPanel westPanel = new JPanel();
   private JPanel southPanel = new JPanel();
   private JPanel eastPanel = new JPanel();
   
   private JButton playButton = new JButton("Play"); //starts the game
   private JButton endButton = new JButton("End"); //ends the game, tells user where to find their results
   private JButton instructionsButton = new JButton("Instructions"); //tells user how the game works and the pay table
   private JButton clearButton = new JButton("Clear"); //clears all entries on the interface, lets user play multiple rounds
   
   private JLabel yourBetLabel = new JLabel("Your bet: ");
   private JLabel currentBetLabel = new JLabel("Current bet: ");
   private JLabel bankLabel = new JLabel("Bank: ");
   private JLabel winLossLabel = new JLabel("Won/Lost");
   private JLabel numSelectedLabel = new JLabel("Numbers selected: ");
   private JLabel numMatchedLabel = new JLabel("Matched: ");
   
   private JTextField betTF = new JTextField(4); //displays the user's selected bet
   private JTextField numSelectedTF = new JTextField(2); //displays the number of buttons the user selected
   private JTextField numMatchedTF = new JTextField(2); //displays the number of buttons matched
   private static JTextField winLossTF = new JTextField(10); //static to interact with calculate()
   private static JTextField bankTF = new JTextField(8); //static to interact with calculate()
   
   //bet dropdown
   private String[] betAmounts = {"$10", "$20", "$50", "$100"};
   private JComboBox betCB = new JComboBox(betAmounts);
   private JButton[] buttonArray = new JButton[SIZE];
   
   //arrays for user selected numbers and computer selected numbers
   private static int[] userNumbers = new int[10]; //static to interact with write()
   private static int[] cpuNumbers = new int[10]; //static to interact with write()
   
   //colors for number buttons
   private Color gray = new Color(210, 210, 210); //color for unselected
   private Color red = new Color(230, 115, 125); //color for user selected
   private Color blue = new Color(115, 150, 220); //color for cpu selected
   private Color green = new Color(125, 180, 125); //color for both selected
   private Color purple = new Color(225, 225, 250); //color for panel backgrounds
   
   //counts how many buttons the user selects
   private int count = 0;
   
   //static to interact with calculate()
   private static int currentBet = 0;
   private static int hits = 0; //counts how many hits/matches are made
   private static int bank = 100; //starting amount in user's bank is $100 in order for a valid bet to be made
   private static int winnings = 0; 
   
   private boolean userSelected = false;
   private boolean betSelected = false;
   
   //static to interact with write()
   private static PrintWriter pw = null; //creates PrintWriter
   private static FileWriter fw = null; //creates FileWriter

   //creates main window
   public EasyKeno() {
      this.setSize(WIDTH, HEIGHT);
      this.setLocationRelativeTo(null); //opens program in center of screen
      this.setResizable(true); //allows window to be resized
      this.setTitle("Keno");
      this.setLayout(new BorderLayout());
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
   
   //sets up interface of main window
   public void setUpGUI() {    
      //creates new ActionListener and adds it to the interface buttons
      ActionListener ears = new MyListener();
      playButton.addActionListener(ears);
      endButton.addActionListener(ears);
      instructionsButton.addActionListener(ears);
      clearButton.addActionListener(ears);
      betCB.addActionListener(ears);
      
      //creates number buttons 1-40
      numbers.setLayout(new GridLayout(10,4,0,0));
      for(int i = 0; i < SIZE; i++){
         buttonArray[i] = new JButton(String.valueOf(i+1)); //labels buttons 1-40
         buttonArray[i].setBackground(gray);
         buttonArray[i].setFont(new Font("Arial", Font.BOLD, 16)); //bold for readability
         buttonArray[i].addActionListener(ears); //adds ActionListener to number buttons
         numbers.add(buttonArray[i]);
      }
      this.add(numbers, BorderLayout.CENTER); 
      
      //sets up north JPanel
      northPanel.setLayout(new FlowLayout());
      northPanel.setBackground(purple); //sets background color of panel as purple
      //sets button size and font of instructions button
      instructionsButton.setPreferredSize(new Dimension (120,50));
      instructionsButton.setFont(new Font("Arial", Font.BOLD, 14));
      //sets default text and font of numbers selected label and text field
      numSelectedTF.setText("0");
      numSelectedTF.setEditable(false);
      numSelectedTF.setFont(new Font("Arial", Font.PLAIN, 14));
      numSelectedLabel.setFont(new Font("Arial", Font.BOLD, 14));
      //sets default text and font of numbers matched label and text field
      numMatchedTF.setText("0");
      numMatchedTF.setEditable(false);
      numMatchedTF.setFont(new Font("Arial", Font.PLAIN, 14));
      numMatchedLabel.setFont(new Font("Arial", Font.BOLD, 14));
      //adds labels, text fields, and button to north JPanel
      northPanel.add(numSelectedLabel);
      northPanel.add(numSelectedTF);
      northPanel.add(numMatchedLabel);
      northPanel.add(numMatchedTF);
      northPanel.add(instructionsButton);
      this.add(northPanel, BorderLayout.NORTH); 
      
      //sets up west JPanel
      westPanel.setLayout(new FlowLayout());
      westPanel.setBackground(purple); //sets background color of panel as purple
      //sets font for bet dropdown label
      yourBetLabel.setFont(new Font("Arial", Font.BOLD, 14));
      //sets font for bet dropdown
      betCB.setFont(new Font("Arial", Font.PLAIN, 14));
      //adds label and dropdown to west JPanel
      westPanel.add(yourBetLabel);
      westPanel.add(betCB);
      betCB.setSelectedIndex(-1); //set index to -1 to show a blank space as the default
      this.add(westPanel, BorderLayout.WEST);
      
      //sets up south JPanel
      southPanel.setLayout(new FlowLayout());
      southPanel.setBackground(purple); //sets background color of panel as purple
      //sets default text and font for bet text field and label
      betTF.setText("$0");
      betTF.setEditable(false);
      betTF.setFont(new Font("Arial", Font.PLAIN, 14));
      currentBetLabel.setFont(new Font("Arial", Font.BOLD, 14));
      //sets text and font for bank text field and label
      bankTF.setText("$" + String.valueOf(bank));
      bankTF.setEditable(false);
      bankTF.setFont(new Font("Arial", Font.PLAIN, 14));
      bankLabel.setFont(new Font("Arial", Font.BOLD, 14));
      //sets size and font of play button
      playButton.setPreferredSize(new Dimension (100,50));
      playButton.setFont(new Font("Arial", Font.BOLD, 14));
      playButton.setEnabled(false); //play button disabled by default
      //sets size and font of end button
      endButton.setPreferredSize(new Dimension (100,50));
      endButton.setFont(new Font("Arial", Font.BOLD, 14));
      //sets size and font of clear button
      clearButton.setPreferredSize(new Dimension (100,50));
      clearButton.setFont(new Font("Arial", Font.BOLD, 14));
      //adds labels, text fields, and buttons to south JPanel
      southPanel.add(currentBetLabel);
      southPanel.add(betTF);
      southPanel.add(bankLabel);
      southPanel.add(bankTF);
      southPanel.add(playButton);
      southPanel.add(endButton);
      southPanel.add(clearButton);
      this.add(southPanel, BorderLayout.SOUTH);
      
      //sets up east JPanel
      eastPanel.setLayout(new FlowLayout());
      eastPanel.setBackground(purple);
      //sets text and font of win/loss label and text field
      winLossLabel.setFont(new Font("Arial", Font.BOLD, 14));
      winLossTF.setText("$" + String.valueOf(winnings));
      winLossTF.setEditable(false);
      winLossTF.setFont(new Font("Arial", Font.PLAIN, 14));
      //adds label and text field to east JPanel
      eastPanel.add(winLossLabel);
      eastPanel.add(winLossTF);
      this.add(eastPanel, BorderLayout.EAST);
   }

   private class MyListener implements ActionListener{
      public void actionPerformed(ActionEvent event){
         //shows instructions in a pop-up window
         if(event.getSource() == instructionsButton){ //if instructions button is clicked
            JOptionPane.showMessageDialog(new JFrame(), 
               "Welcome to Easy Keno!" +
               "\nTo start, please select a bet amount and 10 numbers. Both can be deselected and reselected." +
               "\nThe play button will be enabled if these conditions are met." +
               "\nWhen the play button is pressed, the program will generate 10 random numbers." + 
               "\nIf the numbers selected by you and the CPU match, this will count as a hit." +
               "\n" +
               "\nButton Colors" +
               "\nGray        Buttons not selected by the user or the computer" +
               "\nRed         Buttons selected by the user but not the computer" +
               "\nBlue        Buttons not selected by the user but selected by the computer" +
               "\nGreen     Buttons selected both by the user and the computer" +
               "\n" +
               "\nHits              Win" +
               "\n0                 bet x 2" +
               "\n3                 bet x 1" +
               "\n4                 bet x 2" +
               "\n5                 bet x 3" +
               "\n6                 bet x 7" +
               "\n7                 bet x 30" +
               "\n8                 bet x 200" +
               "\n9                 bet x 1,000" +
               "\n10               bet x 10,000" +
               "\n" +
               "\nYour bank will show your total amount of money." +
               "\nWon/Lost will show your earnings for the current round.", 
               "Instructions", JOptionPane.INFORMATION_MESSAGE);
         }
         else if(event.getSource() == betCB){ //if bet dropdown is clicked
            //gets the selected amount from the combobox
            String s = (String)betCB.getSelectedItem();
            
            try{ //try catch for null pointer exception
            //updates the user's current bet amount
               switch(s){
                  case "$10":
                     currentBet = 10;
                     betTF.setText("$10");
                     break;
                  case "$20":
                     currentBet = 20;
                     betTF.setText("$20");
                     break;
                  case "$50":
                     currentBet = 50;
                     betTF.setText("$50");
                     break;
                  case "$100":
                     currentBet = 100;
                     betTF.setText("$100");
                     break;
               }
               betSelected = true;
            }
            catch(NullPointerException npe){
               System.out.println(npe);
            }
         }
         else if(event.getSource() == playButton){ //if play button is clicked
            //disables bet dropdown and play button, forces user to press clear to start a new round
            betCB.setEnabled(false);
            betSelected = false; 
            playButton.setEnabled(false);  
            
            Random rand = new Random();
            //generates a random number from 1-40 and checks if there are any duplicates
            for(int i = 0; i < 10; i++){
               int randomInt = rand.nextInt(40) + 1;
               boolean isDuplicate = false;
               for(int j = 0; j < i; j++){
                  if(cpuNumbers[j] == randomInt){
                     isDuplicate = true;
                     i--;
                     break;
                  }
               }
               if(isDuplicate == false){
                  cpuNumbers[i] = randomInt;
                  //System.out.println(cpuNumbers[i]);
               }
            }
            
            for(int i = 0; i < 10; i++){
               buttonArray[cpuNumbers[i]-1].setBackground(blue); //changes the color of the randomly selected numbers to blue
            }
            
            //checks both arrays for matching numbers
            for(int i = 0; i < 10; i++){
               for(int j = 0; j < 10; j++){
                  if(cpuNumbers[i] == userNumbers[j]){
                     buttonArray[userNumbers[j]-1].setBackground(green); //changes the color of matching number buttons to green
                     hits++; //updates hits counter
                     numMatchedTF.setText(String.valueOf(hits));     
                  }
               }
            }
            //System.out.println("Hits: " + hits);
            //System.out.println("Win: " + calculate());
            //System.out.println("Total: " + bank);
         
            //calculates winnings
            calculate();
            //writes results to output file
            write();            
         }
         else if(event.getSource() == endButton){ //if end button is clicked        
            betSelected = false;
            
            //disables number buttons
            for(int i = 0; i < SIZE; i++){
               buttonArray[i].setEnabled(false);
            }
            //disables buttons for play, end, clear, instructions, and the bet dropdown
            playButton.setEnabled(false);
            endButton.setEnabled(false);
            clearButton.setEnabled(false);
            instructionsButton.setEnabled(false);
            betCB.setEnabled(false);
            
            JOptionPane.showMessageDialog(new JFrame(), 
               "Thank you for playing Easy Keno! Your results can be found in KenoFile.txt.", "End", JOptionPane.PLAIN_MESSAGE);
            
            //try catch for null pointer exception if nothing is written to the file
            try{
            //closes PrintWriter
               pw.close();
            }
            catch(NullPointerException npe){
               System.out.println(npe);
            }
         }
         else if(event.getSource() == clearButton){ //if clear button is clicked
            //resets variables to zero
            count = 0;
            hits = 0;
            winnings = 0;
            currentBet = 0;
            
            //resets displayed text to 0
            numSelectedTF.setText("0");
            numMatchedTF.setText("0");
            winLossTF.setText("$" + String.valueOf(winnings));
            betTF.setText("$0");
            
            //resets bet selection
            betCB.setSelectedIndex(-1);
            betSelected = false;
            betCB.setEnabled(true);

            //try catch for index out of bounds exception
            try{
            //clears userNumbers[] and cpuNumbers[], resets button colors to default
               for(int i = 0; i < 10; i++){
                  buttonArray[userNumbers[i]-1].setBackground(gray);
                  buttonArray[cpuNumbers[i]-1].setBackground(gray);
                  userNumbers[i] = count;
                  cpuNumbers[i] = 0;
               }
            }
            catch(IndexOutOfBoundsException ioe){
               System.out.println(ioe);
            }
         
            //reenables all number buttons
            for(int i = 0; i < SIZE; i++){
               buttonArray[i].setEnabled(true);
            }
         }
         else{ //if number buttons are clicked
            for(int i = 0; i < SIZE; i++){
               if(event.getSource() == buttonArray[i]){
                  //checks if the user-selected number is in array
                  for(int j = 0; j < 10; j++){
                     if(i + 1 == userNumbers[j]){
                        for(int k = j; k < count-1; k++){ //shifts array
                           userNumbers[k] = userNumbers[k+1];
                        }
                        buttonArray[i].setBackground(gray); //changes number buttons to gray if deselected
                        count--;
                        userNumbers[count] = 0;
                        userSelected = false;
                        
                        //if deselecting a number after reaching 10, enables all other numbers to let user choose again
                        if(count < 10){
                           for(int k = 0; k < SIZE; k++){
                              buttonArray[k].setEnabled(true);
                           }
                        }
                        break;    
                     }
                     else{
                        userSelected = true;
                     }
                  } //end of second for loop
                  
                  if(userSelected){
                     buttonArray[i].setBackground(red); //changes number buttons to red if selected
                     userNumbers[count] = i + 1;
                     count++; //updates count for how many numbers the user selected
                     numSelectedTF.setText(String.valueOf(count));
                     
                     //disables number buttons after user chooses 10 numbers
                     if(count == 10){
                        for(int j = 0; j < SIZE; j++){
                           buttonArray[j].setEnabled(false);
                           //for(int k = 0; k < 10; k++){
                              //if(userNumbers[k] == j + 1){
                                 //buttonArray[j].setEnabled(true);
                              //}
                           //}
                        }
                     }  
                  }
                  
               }
            } //end of first for loop
         }
         //enables the play button when the user selects 10 numbers and a valid bet
         if(count == 10 && betSelected == true){
            playButton.setEnabled(true);
         }else{
            playButton.setEnabled(false);
         }
         
      } 
   }
   
   /**
   *This method calculates the pay for each hit made
   *and displays the information in the corresponding text fields.
   *
   *@return int winnings   The amount won/loss for the current round.
   */
   public static int calculate(){
      switch(hits){
         case 0:
            winnings = currentBet * 2;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 1:
            bank -= currentBet;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 2:
            bank -= currentBet;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 3:
            winnings = currentBet * 1;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 4:
            winnings = currentBet * 2;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 5:
            winnings = currentBet * 3;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 6:
            winnings = currentBet * 7;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 7:
            winnings = currentBet * 30;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 8:
            winnings = currentBet * 200;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 9:
            winnings = currentBet * 1000;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;
         case 10:
            winnings = currentBet * 10000;
            bank += winnings;
            winLossTF.setText("$" + String.valueOf(winnings));
            bankTF.setText("$" + String.valueOf(bank));
            break;           
      }
      return winnings;
   }
   
   /**
   *This method outputs the results of the game to a text file.
   *Prints results for each round played.
   */
   public static void write(){
      try{
         File f = new File("KenoFile.txt");
         fw = new FileWriter(f, true); //appends output to file
         pw = new PrintWriter(fw);
      }catch(FileNotFoundException e){
         System.out.println("No file found.");
      }catch(IOException e){
         System.out.println("Failed to write file.");
      }
      
      pw.println("User selected the following numbers: ");
      for(int i = 0; i < 10; i++){
         pw.print(userNumbers[i] + " ");
      }
      
      pw.println("");
      pw.print("User matched the following numbers: ");
      for(int i = 0; i < 10; i++){
         for(int j = 0; j < 10; j++){
            if(userNumbers[i] == cpuNumbers[j]){
               pw.print(userNumbers[i] + " ");
            }
         }
      }
      pw.println("");
      pw.println("Bet amount: $" + currentBet);
      pw.println("Total number of matches: " + hits);
      pw.println("User won/lost $" + winnings + " for this round.");
      pw.println("");
      
      //closes PrintWriter
      pw.close();
   }
   
   /**
   *The main method calls setUpGUI() to create the interface.
   *
   */
   public static void main (String[] arg){
      EasyKeno keno = new EasyKeno();
      keno.setUpGUI();
      keno.setVisible(true);
   }
}
