# Leader Election Simulator

-This README provides instructions to compile and run the Java code for simulating and evaluating the LCR and HS leader election algorithms
in a bidirectional ring network.  
-More details about the problem can be found in the evaluation report (PDF).  
-The simulator allows users to choose between a single simulation (with user-specified ring size, algorithm choice, and ID assignment)
or multiple simulations (testing predefined ring sizes for a chosen algorithm and ID assignment),  
-The simulator tracks rounds, messages, and correctness.

## Requirements:
- Java Development Kit (JDK) 8 or higher installed on your system.
- Command-line access (e.g., Windows Command Prompt, Linux Terminal, macOS Terminal).

## Files Included:
- LeaderElectionSimulator.java
- LCRAlgorithm.java
- HSAlgorithm.java
- Message.java
- Processor.java
- RingNetwork.java
- LeaderElectionAlgorithm.java

## Compilation:
1. Ensure all Java files are in the same directory (no packages used).
2. Open a terminal or command prompt in that directory.
3. Run the following command to compile all files:
   javac *.java

This will generate .class files for all Java files.

## Running the Simulator:
1. After compilation, run the simulator using:
   java LeaderElectionSimulator

2. The program will prompt for input:
   - "Enter run type (single, multiple)": Type "single" or "multiple" and press Enter.
   - "Enter algorithm (LCR, HS)": Type "LCR" or "HS" and press Enter.
   - "Enter ID assignment type (ascending, descending, random)": Type "ascending", "descending", or "random" and press Enter.

3. Depending on the run type:
   - **Single Run**:
     - "Enter ring size (n)": Enter a positive integer and press Enter.
     - The simulator runs once, displaying:
       "Ring size: [n], Algorithm: [LCR/HS], ID type: [ascending/descending/random]"
       "Rounds: [number], Messages: [number], Correct: [Yes/No]"
   - **Multiple Runs**:
     - The simulator automatically runs for ring sizes 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, and 1000, displaying results for each run.
