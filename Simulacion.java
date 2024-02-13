/**
 * 
 */
package com.elizalde.simulacion;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.elizalde.simulacion.input.Keyboard;
import com.elizalde.simulacion.tablas.TablaBloqueados;
import com.elizalde.simulacion.tablas.TablaDeResultados;
import com.elizalde.simulacion.tablas.TablaEnEjecucion;
import com.elizalde.simulacion.tablas.TablaLotes;
import com.elizalde.simulacion.tablas.TablaPaginas;
import com.elizalde.simulacion.tablas.TablaTiempos;
import com.elizalde.simulacion.timer.Timers;
import com.elizalde.simulacion.virtual.MemoriaSuspendida;


/**
 * @author Elizalde Loera Felipe de Jesus
 *
 */

public class Simulacion extends JFrame implements ActionListener, Runnable {

	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	private GridBagLayout layout;
	private GridBagConstraints constraints;

	// Threads
	public Thread ResultsThread;
	boolean running = false;

	// Gui Components
	public JLabel lotesPendientes;
	public static JLabel Cola_de_Listos = new JLabel("Listos");
	public static JLabel Ejecucion = new JLabel("Ejecutando");
	public static JLabel Cola_de_Bloqueados = new JLabel("Bloqueados");
	public static JLabel Terminados = new JLabel("Terminados");
	public JButton buttonAdd;
	public JButton buttonProcesar;
	public JFrame process;

	// Gui Specs
	public static int width = 1200;
	public static int height = 600;
	public static int scale = 3;
	public static String title = "Simulacion";
	public boolean empty = false;

	// Classes
	public TablaLotes tablaLotes;
	public TablaEnEjecucion segundaTabla;
	public TablaDeResultados tercerTabla;
	public TablaBloqueados cuartaTabla;
	public TablaTiempos quintaTabla;
	public MemoriaSuspendida ms;
	public Timers timer;
	Generador generador;

	public boolean isPaused = false;
	public boolean isContinued = false;
	public boolean rowRemoved = false;
	Keyboard key;
	// Data Structures
	public Vector<Vector<String>> info = new Vector<Vector<String>>(), mainTable = new Vector<Vector<String>>();

	public Vector<String>currentBCP = new Vector<String>();
	public Vector<Vector<String>>finalBCP = new Vector<Vector<String>>();
	public Vector<String> ids = new Vector<String>();
	
	
	public Vector<Vector<Integer>> memory = new Vector<Vector<Integer>>(); 
	public int[] etiquetas = new int[30];
	public int globalCounter = 0;
	public int lotCounter = 0;
	public int memoryCounter = 108;
	public int etiquetasAvaiable = 27;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Simulacion simulacion = new Simulacion();
		simulacion.setResizable(true);
		simulacion.setTitle(Simulacion.title);
		simulacion.pack();
		simulacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulacion.setLocationRelativeTo(null);
		simulacion.setVisible(true);

	}

	public Simulacion() {
		key = new Keyboard();
		addKeyListener(key);
		this.setPreferredSize(new Dimension(width,height));
		int isEmpty = ids.capacity();
		if (isEmpty == 0) {
			//System.out.println("Empty");
		}
		//setPreferredSize(new Dimension(width * scale, height*2));
		for(int i = 0; i < etiquetas.length;i++)
		{
			etiquetas[i] = -1;
			
		}
		
		
		layout = new GridBagLayout();
		setLayout(layout);
		
		constraints = new GridBagConstraints();
		ms = new MemoriaSuspendida();
		tablaLotes = new TablaLotes();

		segundaTabla = new TablaEnEjecucion();
		segundaTabla.defaultTable.addRow(new Object[] { "Programa" });
		segundaTabla.defaultTable.addRow(new Object[] { "Operacion" });
		segundaTabla.defaultTable.addRow(new Object[] { "Tiempo Maximo Estimado" });
		segundaTabla.defaultTable.addRow(new Object[] { "Tiempo Transcurrido" });
		segundaTabla.defaultTable.addRow(new Object[] { "Tiempo Restante" });
		segundaTabla.defaultTable.addRow(new Object[] { "Quantum Transcurrido"});
		segundaTabla.defaultTable.addColumn("Datos");

		segundaTabla.tabla.setRowHeight(35);
		tercerTabla = new TablaDeResultados();
		cuartaTabla = new TablaBloqueados();
		quintaTabla = new TablaTiempos();
		lotesPendientes = new JLabel("Numero de procesos en la Cola de Nuevos: 0");
		buttonAdd = new JButton("Agregar Proceso");
		buttonProcesar = new JButton("Procesar");
		timer = new Timers();
		buttonProcesar.addActionListener(this);
		buttonAdd.addActionListener(this);

		// Put all your components in the JPanel
		constraints.fill = GridBagConstraints.PAGE_START;
		
		addComponent(lotesPendientes, 0, 0, 1, 1);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(timer, 0, 4, 1, 1);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(Cola_de_Listos, 1, 0, 1, 1);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(Ejecucion, 1, 2, 1, 1);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(Terminados, 1, 4, 1, 1);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(Cola_de_Bloqueados, 1, 6, 1, 1);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(new JPanel().add(tablaLotes.getScrollPane()), 2, 0, 2, 2);

		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(new JPanel().add(segundaTabla.getScrollPane()), 2, 1, 2, 2);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(new JPanel().add(tercerTabla.getScrollPane()), 2, 3, 2, 2);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(new JPanel().add(cuartaTabla.getScrollPane()), 2, 5, 2, 2);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(new JPanel().add(quintaTabla.getScrollPane()), 5, 0, 8, 2);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(buttonAdd, 7, 0, 4, 2);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(buttonProcesar, 7, 4, 4, 2);
		
		
	}

	public void addComponent(Component component, int row, int column, int width1, int height1) {
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.gridwidth = width1;
		constraints.gridheight = height1;
		layout.setConstraints(component, constraints);
		add(component);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonAdd) {
			this.setVisible(false);
			tercerTabla.clearAll();
			generador = new Generador();
			
			generador.question();
			segundaTabla.setQuantum(generador.getQuantum());
			info = generador.getTable();
			
			copyTable();
		}
		if (e.getSource() == buttonProcesar) {
			if (mainTable.size() > 0) {
				//System.out.println("Lote Counter: " + lotCounter);
				segundaTabla.setOneProcess(mainTable.get(0));
				
				for(int i= 0; i < mainTable.size(); i++)
				{
					updateTable(i);
				}
				tablaLotes.defaultTable.removeRow(0);
				
				timer.startTimer();
				  ///.////////The problem is right here
				//cuartaTabla.setAllinfo(info.get(0));
				segundaTabla.startTimer();
				cuartaTabla.startTimer();
				startTimer();
				buttonProcesar.setEnabled(false);
				buttonAdd.setEnabled(false);
				
			}
		}

	}

	public void copyTable()
	{		
		for(int i = 0; i < info.size();i++)
		{
			System.out.println(info.get(i));
		}
		System.out.println();
		while(memoryCounter > 0  && info.size() > 0)
		{
			
			int currentMemory = checkMemory();
			
			if(currentMemory <= memoryCounter)
			{
				mainTable.addElement(info.get(0));
				System.out.println(info.get(0));
				memoryCounter -= currentMemory;
				fillMemory();
				info.remove(0);
			}
			else
			{
				break;
			}

		}
		updateNumberLotes(info.size());
		System.out.println("Memory Counter: " + memoryCounter);
		this.setVisible(true);
	}
	public int checkMemory()
	{
		int memory = Integer.parseInt(info.get(0).get(12));
		int remainder = memory %4;
		if(remainder != 0)
		{
			remainder = 4-remainder;
		}
		return memory+remainder;
	}
	public int getMemory(int savedMemory)
	{
		
		int remainder = savedMemory %4;
		if(remainder != 0)
		{
			remainder = 4-remainder;
		}
		return savedMemory+remainder;
	}
	public void fillMemory()
	{ 
		int memory = Integer.parseInt(info.get(0).get(12));
		int remainder = memory %4;
		if(remainder != 0)
		{
			remainder = 4-remainder;
		}
		if(((memory + remainder) / 4) <= etiquetasAvaiable)
		{
			int assignCounter = 0;
			Vector<Integer> nameProcess = new Vector<Integer>(); // P for Process
			nameProcess.add(Integer.parseInt(info.get(0).get(0)));
			for(int i = 3; i < etiquetas.length;i++)
			{
				if(etiquetas[i] == -1)
				{
					etiquetas[i] = Integer.parseInt(info.get(0).get(0));
					nameProcess.add(4);
					assignCounter++;
					if(assignCounter >= (memory+remainder) /4)
					{
						if((memory % 4) != 0)
						{
						    nameProcess.set(assignCounter, memory%4);
						}
						break;
					}
					/*
					if( (memory % 4) != 0)
					{
						nameProcess.set(assignCounter, memory%4);
						
					}
					if(assignCounter >= (memory+remainder) /4)
					{
						break;
					}
					*/
				}
			}
			this.memory.add(nameProcess);
			etiquetasAvaiable -= ((memory+remainder)/4);
		}
	}
	
	
	public void updateTable(int processCounter) {
		if(processCounter > -1)
		if (processCounter < info.size() || processCounter < mainTable.size()) {
			int sectionCounter = 0;
			while (sectionCounter < 1) {
				//System.out.println("MainTable Size: " + mainTable.size());
				//System.out.println(mainTable);
				//System.out.println("Process Counter: " + processCounter);
				if(mainTable.get(processCounter).get(11) == "0")
				{
					mainTable.get(processCounter).set(7, Integer.toString(timer.getTime()));
				}
				mainTable.get(processCounter).set(10, Integer.toString(timer.getTime()));
				tablaLotes.defaultTable.addRow(new Object[] { mainTable.get(processCounter).get(0),
						mainTable.get(processCounter).get(4), mainTable.get(processCounter).get(6)});
				globalCounter++;
				sectionCounter++;
			}
		}
		//System.out.println("Time of 6th element: " + info.get(6).get(7));
	}

	public void updateNumberLotes(int numeroLotes) {
		lotCounter = numeroLotes;
		lotesPendientes.setText("Numero de procesos en la Cola de Nuevos: " + numeroLotes);
	}
	

	public synchronized void startTimer() {
		running = true;
		ResultsThread = new Thread(this, "Results");
		ResultsThread.start();
	}

	public synchronized void stop() {
		running = false;
		info.removeAllElements();
		ids.removeAllElements();
		mainTable.removeAllElements();
		buttonProcesar.setEnabled(true);
		buttonAdd.setEnabled(true);
		try {
			timer.stop();
			segundaTabla.stop();
			ResultsThread.join(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void update() throws InterruptedException {
		key.update();
		if (key.p == true && isPaused == false) {
			isPaused = true;
			pauseProgram();
		}
		else if (key.c == true && isPaused == true) {
			isPaused = false;
			continueProgram();
		}
		else if(key.t == true && isPaused == false)
		{
			isPaused = true;
			pauseProgram();
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TablaPaginas paginas = new TablaPaginas(etiquetas,memory,info,mainTable,cuartaTabla.getAllProcess());
		}
		else if(key.b == true && isPaused == false)
		{
			isPaused = true;
			pauseProgram();
			for(int i = 0 ;  i < quintaTabla.defaultTable.getRowCount(); i++)
			{
				quintaTabla.defaultTable.removeRow(0);
				i--;
			}
			
			for(int i = 0; i < info.size(); i++)
			{
				quintaTabla.defaultTable.addRow(new Object[]
						{
							info.get(i).get(0),
							"?",
							"?",
							"?",
							"?",
							"?",
							"?"
						});
			}
			
				if(segundaTabla.getProcessSet())
				{
					quintaTabla.defaultTable.addRow(new Object[]
							{
									
							segundaTabla.getOneProcess().get(0),
							segundaTabla.getOneProcess().get(7),
							"?",
							"?",
							segundaTabla.getOneProcess().get(8),
							segundaTabla.getOneProcess().get(9),
							"?"
							}
					);
					
				}
		
			for(int i = 1; i < mainTable.size();i++)
			{
				if(mainTable.get(i).get(11) == "1")
				quintaTabla.defaultTable.addRow(new Object[]
						{
								mainTable.get(i).get(0),
								mainTable.get(i).get(7),
								"?",
								"?",
								mainTable.get(i).get(8),
								mainTable.get(i).get(9),
								"?"
						});
				else 
					quintaTabla.defaultTable.addRow(new Object[]
							{
									mainTable.get(i).get(0),
									mainTable.get(i).get(7),
									"?",
									"?",
									
									"?",
									mainTable.get(i).get(9),
									"?"
							});
				
			}
			
			//Is Correct  
			for(int i = 0; i < finalBCP.size();i++)
			{
				
				quintaTabla.defaultTable.addRow(new Object[]
				{
						finalBCP.get(i).get(0),
						finalBCP.get(i).get(1),
						finalBCP.get(i).get(2),
						finalBCP.get(i).get(3),
						finalBCP.get(i).get(4),
						finalBCP.get(i).get(5),
						finalBCP.get(i).get(6)			
				});
				
			}
			for(int i = 0; i < cuartaTabla.getAllProcess().size();i++)
			{
				//System.out.println(cuartaTabla.getAllProcess().get(i));
				
				quintaTabla.defaultTable.addRow(new Object[]
				{			
						cuartaTabla.getAllProcess().get(i).get(0),
						cuartaTabla.getAllProcess().get(i).get(7),
						"?",
						"?",
						cuartaTabla.getAllProcess().get(i).get(8),
						cuartaTabla.getAllProcess().get(i).get(9),
						"?"
						
				});
				
			}
			
			/*
			System.out.println("Final BCP size: " + finalBCP.size());
			System.out.println("MainTable size: " + mainTable.size());
			System.out.println("Info size: " + info.size());
			System.out.println("Blocked Processes size: " + cuartaTabla.getAllProcess().size());
			*/
		}
		else if (key.w == true && isPaused == false) {
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			segundaTabla.Error();
		} 
		else if(key.u == true)
		{
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector <String> temp = generador.getGenerateOne();
			int newMemory = getMemory(Integer.parseInt(temp.get(12)));
			//System.out.println("Memory Counter: " +memoryCounter);
			//System.out.println("New Memory: " + newMemory);
			if	(newMemory <= memoryCounter &&  
					segundaTabla.getProcessSet() == true && 
					info.size() == 0)
				{
				//System.out.println("Memory Counter: " +memoryCounter);
					memoryCounter -= newMemory;
					//System.out.println("Memory Counter: " +memoryCounter);
					info.addElement(temp);
					mainTable.add(info.get(0));
					fillMemory();
					info.remove(0);
					updateTable(mainTable.size()-1);
				}
			else if(newMemory <= memoryCounter &&  
					segundaTabla.getProcessSet() == false && 
					info.size() == 0 &&
					mainTable.size() > 0)
			{
				//System.out.println("Memory Counter: " +memoryCounter);
				memoryCounter -= newMemory;
				//System.out.println("Memory Counter: " +memoryCounter);
				info.addElement(temp);
				mainTable.add(info.get(0));
				fillMemory();
				info.remove(0);
				updateTable(mainTable.size()-1);
			}
			else if (newMemory <= memoryCounter && mainTable.size()==0  
					&& segundaTabla.getProcessSet() == false 
					&& info.size() == 0)
			{
				//System.out.println("Memory Counter: " + memoryCounter);
				memoryCounter -= newMemory;
				//System.out.println("Memory Counter: " +memoryCounter);
				temp.set(7, Integer.toString(timer.getTime()));
				info.addElement(temp);
				mainTable.addElement(info.get(0));
				fillMemory();
				info.remove(0);
				mainTable.get(0).set(10, String.valueOf(timer.getTime()));
				segundaTabla.setOneProcess(mainTable.get(0));
				
				segundaTabla.setBlocked(false);
				segundaTabla.running = true;
				
			}
			else {
					info.add(temp);
					updateNumberLotes(lotCounter+1);
				}
		}
		else if (key.e == true /*&& info.size() >= 0
				&& tablaLotes.defaultTable.getRowCount() +
				segundaTabla.programCounter +  
				cuartaTabla.counter <=5  && segundaTabla.getProcessSet() == true*/) {
			
			if( segundaTabla.getProcessSet() == true &&
					Integer.parseInt(segundaTabla.getOneProcess().get(6))> 1 &&
					segundaTabla.getQuantum() < generador.getQuantum()-1)
			{
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
				
				segundaTabla.running = false;
				segundaTabla.setBlocked(true);
				
				if(tablaLotes.defaultTable.getRowCount() > 0)
				{
					tablaLotes.defaultTable.removeRow(0);
				}
				
				//System.out.println("Before Quantum Counter: " + segundaTabla.getQuantum());
				while(!segundaTabla.isBreaking())
				{
					try {
						TimeUnit.MILLISECONDS.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
				}
				//System.out.println("One Process:" + segundaTabla.getOneProcess());
				//System.out.println("Quantum Counter: " + segundaTabla.getQuantum());
				if(segundaTabla.getQuantum() < generador.getQuantum())
				{	
					cuartaTabla.setAllInfo(segundaTabla.getOneProcess());
				
					cuartaTabla.setValues();
					if(!mainTable.isEmpty())
					{
					    mainTable.remove(0);
					}
					
					segundaTabla.deleteOneProcess();
					segundaTabla.clearTable();
					
					//if(tablaLotes.defaultTable.getRowCount() + segundaTabla.programCounter +  cuartaTabla.counter <=5)
						if(mainTable.size() > 0)
						{//System.out.println("Original Value:" + mainTable.get(0));
							segundaTabla.setOneProcess(mainTable.get(0));
							segundaTabla.setBlocked(false);
							segundaTabla.running = true;
						}	
				}
				else{
					segundaTabla.programCounter = 0;
					segundaTabla.setProcessChanged();
					segundaTabla.setBlocked(false);
					segundaTabla.running = true;
					
				}
				
			}
			else
			{
				System.out.println("Here Tecla U");
			}
			
		}
		else if(key.d == true)
		{
			if(cuartaTabla.counter > 0)
			{
			cuartaTabla.suspendido = true;
			//MemoriaSuspendida ms = new MemoriaSuspendida();
			Vector<String>temp = cuartaTabla.getProcess();
			int savedMemory = Integer.parseInt(temp.get(12));
			etiquetasAvaiable += getMemory(savedMemory)/4;
			deleteProcess(Integer.parseInt(temp.get(0)));
			ms.addProcess(temp);
			memoryCounter += getMemory(savedMemory);
			
			
			cuartaTabla.deleteVector();
			}
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(key.g == true)
		{
			//MemoriaSuspendida ms = new MemoriaSuspendida();
			Vector<String> temp = ms.getProcess();
			System.out.println(temp);
			if(temp.size() > 0)
			{
					int newMemory = getMemory(Integer.parseInt(temp.get(12)));
					
					if(newMemory <= memoryCounter 
							&& segundaTabla.getProcessSet() == true
							&& info.size() == 0)
					{
						System.out.println("First");
						memoryCounter -= newMemory;
						info.addElement(temp);
						mainTable.add(info.get(0));
						fillMemory();
						info.remove(0);
						updateTable(mainTable.size()-1);
					}
					else if(newMemory <= memoryCounter 
							&& segundaTabla.getProcessSet() == false
							&& info.size() == 0 
							&& mainTable.size() > 0)
					{
						System.out.println("Second");
						memoryCounter -= newMemory;
						info.addElement(temp);
						mainTable.add(info.get(0));
						fillMemory();
						info.remove(0);
						updateTable(mainTable.size()-1);
					}
					else if(newMemory <= memoryCounter 
							&& mainTable.size() == 0
							&& segundaTabla.getProcessSet() == false
							&& info.size() == 0)
					{
						System.out.println("Third");
						memoryCounter -= newMemory;
						temp.set(7, Integer.toString(timer.getTime()));
						info.addElement(temp);
						mainTable.addElement(info.get(0));
						fillMemory();
						info.remove(0);
						mainTable.get(0).set(10, String.valueOf(timer.getTime()));
						segundaTabla.setOneProcess(mainTable.get(0));
						segundaTabla.setBlocked(false);
						segundaTabla.running = true;
					}
					else
					{
						System.out.println("Fourth");
						ms.addFirst(temp);
					}
		}
			else
			{
				System.out.println("Fifth");
			}
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
		else if(key.l == true)
		{
			
		}
	}

	@SuppressWarnings("deprecation")
	public void pauseProgram() {
		timer.timerThread.suspend();
		segundaTabla.executionThread.suspend();
		cuartaTabla.blockThread.suspend();
	}

	@SuppressWarnings("deprecation")
	public void continueProgram() {
		timer.timerThread.resume();
		segundaTabla.executionThread.resume();
		cuartaTabla.blockThread.resume();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		requestFocus();
		while (running) {
			//System.out.println(memoryCounter);
			try {
				if(cuartaTabla.getWaitOver())
				{
					
					mainTable.addElement(cuartaTabla.getProcess());
					cuartaTabla.deleteVector();
					
					if(segundaTabla.getProcessSet() == false)
					{
						mainTable.get(0).set(10, String.valueOf(timer.getTime()));
						segundaTabla.setOneProcess(mainTable.get(0));	
						
						//mainTable.remove(0);
					}
					else
					{
					    updateTable(mainTable.size()-1);
					}
					
					segundaTabla.setBlocked(false);
					segundaTabla.running = true;
					cuartaTabla.setWaitOver();
				}
				update();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				// TimeUnit.SECONDS.sleep(0.5);
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			segundaTabla.setTime(timer.getTime());
			
			if (segundaTabla.getProcessChanged()) {
				
				segundaTabla.running = true;
			
				int timeLeft  = Integer.parseInt(segundaTabla.getOneProcess().get(6));
				//System.out.println("TimeLeft: " + timeLeft);
				if(timeLeft <= 0)
				{
					
					
					tercerTabla.defaultTable.addRow(new Object[] {
							mainTable.get(0).get(0), mainTable.get(0).get(1)
									+ mainTable.get(0).get(2) + mainTable.get(0).get(3),
							segundaTabla.getResult() });
					
					int finishTime = timer.getTime();
					mainTable.set(0, segundaTabla.getOneProcess());
					
					
					Vector<String> processFinished = new Vector<String>();
					processFinished.addElement(mainTable.get(0).get(0));
					processFinished.addElement(mainTable.get(0).get(7));
					processFinished.addElement(String.valueOf(finishTime));
					processFinished.addElement(String.valueOf(finishTime-Integer.parseInt(mainTable.get(0).get(7))));
					processFinished.addElement(mainTable.get(0).get(8));
						int correctWaitTime = finishTime-Integer.parseInt(mainTable.get(0).get(7))-Integer.parseInt(mainTable.get(0).get(5));
					processFinished.addElement(String.valueOf(correctWaitTime));
					//processFinished.addElement(mainTable.get(0).get(9));
					processFinished.addElement(mainTable.get(0).get(5));
					finalBCP.addElement(processFinished);
					//quintaTabla.defaultTable.addRow(processFinished);
					int savedMemory = Integer.parseInt(mainTable.get(0).get(12));
					etiquetasAvaiable +=getMemory(savedMemory)/4;
					//System.out.println("Etiquetas Disponibles: " + etiquetasAvaiable);
					deleteProcess(Integer.parseInt(mainTable.get(0).get(0)));
					mainTable.remove(0);
					segundaTabla.setProcessChanged();
					memoryCounter+= getMemory(savedMemory);
					int processAdded = 0;
					if(info.size() > 0)
					{
						
						
						while(memoryCounter > 0  && info.size() > 0)
						{
							
							int currentMemory = checkMemory();
							if(currentMemory <= memoryCounter)
							{
								processAdded++;
								mainTable.addElement(info.get(0));
								//System.out.println(info.get(0));
								memoryCounter -= currentMemory;
								fillMemory();
								info.remove(0);
							}
							else
							{
								break;
							}

						}
						
						//mainTable.addElement(info.get(0));
						//info.remove(0);
						segundaTabla.setOneProcess(mainTable.get(0));
					}
					else if(mainTable.size() > 0)
					{
						segundaTabla.setOneProcess(mainTable.get(0));
					}
					
					
					if(tablaLotes.defaultTable.getRowCount() > 0)
						tablaLotes.defaultTable.removeRow(0);
					
					
					while(processAdded > 0)
					{	
						updateTable(mainTable.size()-processAdded);
						processAdded--;
						
					}
					if(segundaTabla.getProcessSet() == true)
					{
						if(tablaLotes.defaultTable.getRowCount() > 0)
						if(segundaTabla.getProcessSet() == true && tablaLotes.defaultTable.getValueAt(0, 0) == segundaTabla.getOneProcess().get(0))
						{
							tablaLotes.defaultTable.removeRow(0);
						}
					}
					
					
						if(lotCounter > 0 )
						updateNumberLotes(lotCounter - 1);
						
					if (info.size()==0 && mainTable.size() == 0 && cuartaTabla.defaultTable.getRowCount() == 0 && ms.processCounter <= 0) {
						//System.out.println("Stopped all processes");
						for(int i = 0 ;  i < quintaTabla.defaultTable.getRowCount(); i++)
						{
							quintaTabla.defaultTable.removeRow(0);
							i--;
						}
						for(int i = 0; i < finalBCP.size();i++)
						{
							quintaTabla.defaultTable.addRow(finalBCP.get(i));
						}
						stop();
					}
				}
				else
				{
					if(tablaLotes.defaultTable.getRowCount() >0)
						tablaLotes.defaultTable.removeRow(0);
					Vector <String>temp = segundaTabla.getOneProcess();
					mainTable.remove(0);
					segundaTabla.setProcessChanged();
					mainTable.add(temp);
					segundaTabla.setOneProcess(mainTable.get(0));
					
					updateTable(mainTable.size()-1);
					
				}
			}

		}
	}
	void deleteProcess(int p)
	{
		for(int i = 0; i < etiquetas.length;i++)
		{
			if(etiquetas[i] == p)
			{
				etiquetas[i] = -1;
			}
		}
		for(int i = 0; i < memory.size();i++)
		{
			if(memory.get(i).get(0) == p)
			{
				memory.remove(i);
				break;
			}
		}
	}

}
