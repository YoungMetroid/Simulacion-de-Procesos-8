package com.elizalde.simulacion.tablas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class TablaPaginas extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int width = 400, height = 600;
	private static String  frameName = "Pagina de Procesos";
	private static String[] names = {"Listos","Memoria", "Nuevos","Memoria"};
	public JPanel pagina,panel,tablee;
	public JProgressBar progressBar[] = new JProgressBar[30];
	public JLabel labels[] = new JLabel[30]; 
	public JLabel etiquetas[] = new JLabel[30];
	public int nombreEtiquetas[];
	public JLabel process[] = new JLabel[30];
	public int labelCounter = 0;
	public int barCounter = 0;
	public Vector<Vector<Integer>> info;
	public Vector<Vector<String>> nuevos,listos,blocked;
		
	public TablaAbstracta t;
	public TablaPaginas(int[] nombreEtiquetas,Vector<Vector<Integer>> info,
			Vector<Vector<String>>nuevos,Vector<Vector<String>> listos,
			Vector<Vector<String>>blocked)
	{
		
		
		this.nombreEtiquetas = nombreEtiquetas;
		this.info = info;
		this.nuevos = nuevos;
		this.listos = listos;
		this.blocked = blocked;
		System.out.println(info);
		System.out.println("Info Size" + info.size());
		this.setLayout(null);
		this.setTitle(frameName);
		this.setPreferredSize(new Dimension(width,height));
		this.setSize(new Dimension(width,height));
		this.setResizable(true);
		pagina =  new JPanel(null);
		pagina.setSize(new Dimension((int) (width*0.50),height-20));
		pagina.setLayout(new GridLayout(12,6,0,0));
		pagina.setBackground(Color.BLACK);
		
		panel = new JPanel(null);
		panel.setSize(new Dimension((int) (width*0.20),height-20));
		panel.setLayout(new GridLayout(30,2,0,0));
		panel.setBackground(Color.BLACK);
		panel.setLocation((int)(width*0.50), 0);
		
		t =  new TablaAbstracta();
		tablee = new JPanel(null);
		
		
		tablee.setSize(new Dimension((int) (width*0.80),height-20));
		tablee.setLayout(new GridLayout(1,1,0,0));
		tablee.setLocation((int)(width *0.70),0);
		
		t.defaultTable.setColumnIdentifiers(names);
		tablee.add(t.getScrollPane());
		for(int i = 0; i < listos.size();i++)
		{
			t.defaultTable.addRow(new Object[]{this.listos.get(i).get(0),this.listos.get(i).get(12)});	
		}
		for(int i = 0; i < blocked.size();i++)
		{
			t.defaultTable.addRow(new Object[]{this.blocked.get(i).get(0),this.blocked.get(i).get(12)});	
		}
		for(int i = 0; i < nuevos.size();i++)
		{
			t.defaultTable.addRow(new Object[]{"","",this.nuevos.get(i).get(0),this.nuevos.get(i).get(12)});	
		}
		
		
		
		for(int i = 0; i < 30; i++)
		{
			etiquetas[i] = new JLabel();
			etiquetas[i].setText(String.valueOf(i));
			etiquetas[i].setForeground(Color.cyan);
			process[i] = new JLabel();
			panel.add(etiquetas[i]);
			panel.add(process[i]);
		}
		
		for(int j = 0; j < 6; j++)
		{
			for(int i = 0; i < 5;i++)
			{
				labels[labelCounter] = new JLabel();
				labels[labelCounter].setText(String.valueOf(labelCounter));
				labels[labelCounter].setForeground(Color.cyan);
				pagina.add(labels[labelCounter]);
				labelCounter++;
				
			}
			
			for(int i = 0; i < 5;i++)
			{
				progressBar[barCounter] = new JProgressBar();
				progressBar[barCounter].setMinimum(0);
				progressBar[barCounter].setMaximum(4);
				pagina.add(progressBar[barCounter]);
				
				barCounter++;
			}
			
		}
		
		progressBar[0].setValue(4);
		progressBar[1].setValue(4);
		progressBar[2].setValue(4);
		progressBar[0].setForeground(Color.GRAY);
		progressBar[1].setForeground(Color.GRAY);
		progressBar[2].setForeground(Color.GRAY);
		for(int i = 3; i < process.length;i++)
		{
			if(nombreEtiquetas[i] != -1)
			process[i].setText(String.valueOf(nombreEtiquetas[i]));
		}
		
		for(int i = 0;i < info.size();i++)
		{
			int currentPosition = 0;
			for(int j = 1;j < info.get(i).size();j++)
			{
				for(int k =currentPosition; k < nombreEtiquetas.length; k++)
				{
					if(nombreEtiquetas[k] == info.get(i).get(0))
					{
						progressBar[k].setValue(info.get(i).get(j));
						currentPosition = k+1;;
						break;
					}			
				}
			}
		}
		pagina.setVisible(true);
		panel.setVisible(true);
		tablee.setVisible(true);
		
		this.add(pagina);
		this.add(panel);
		this.add(tablee);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		
		
		
	}
}
