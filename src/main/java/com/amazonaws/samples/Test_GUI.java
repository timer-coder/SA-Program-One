package com.amazonaws.samples;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class Test_GUI {

	private JFrame frame;
	private JTextArea message_area;
	private JTextArea point_area;
	//private JTextArea choice_area;
	static String url;
	static AmazonSQS sqs;
	private static String messages;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test_GUI window = new Test_GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Test_GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		point_area=new JTextArea();
		point_area.setRows(10);
		point_area.setColumns(10);
		frame.getContentPane().add(point_area,BorderLayout.NORTH);
		
		message_area=new JTextArea();
		message_area.setRows(10);
		message_area.setColumns(10);
		frame.getContentPane().add(message_area);
		
		point_area.setText("Please input the name of the queue you create:");
		
		JButton btnCreate = new JButton("Create");
		btnCreate.setSize(250, 500);
		btnCreate.setBounds(100, 100, 100, 30);
		frame.getContentPane().add(btnCreate, BorderLayout.EAST);
		btnCreate.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				sqs =AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
				String QueueName = null;
				QueueName=message_area.getText();
				url=sqs.createQueue(new CreateQueueRequest(QueueName)).getQueueUrl();
				System.out.println("Create");
				point_area.append("\nPlease clear your window\nPlease input the message you want to send:");
			};
		});
		
		JButton btnSend = new JButton("Send");
		btnSend.setSize(250, 500);
		btnSend.setBounds(100, 100, 100, 30);
		frame.getContentPane().add(btnSend, BorderLayout.WEST);
		btnSend.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				messages=message_area.getText();
				System.out.print(messages+"\n");
				sqs.sendMessage(new SendMessageRequest(url,messages));
				System.out.println("Send");
				point_area.append("\nSend successfully!");
			};
		});
		
		JButton btnRead = new JButton("Read");
		btnRead.setSize(250, 500);
		btnRead.setBounds(100, 100, 100, 30);
		frame.getContentPane().add(btnRead, BorderLayout.SOUTH);
		btnRead.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				while (true) 
				{
					List<Message> msgs = sqs.receiveMessage(
					     new ReceiveMessageRequest(url).withMaxNumberOfMessages(1)).getMessages();
					if (msgs.size() > 0) 
					{
					   Message message = msgs.get(0);
					   System.out.println("The message is " + message.getBody());
					   point_area.append("\nThe message is " + message.getBody());
					   sqs.deleteMessage(new DeleteMessageRequest(url, message.getReceiptHandle()));
					} 
					else 
					{
					    System.out.println("nothing found!");
					    point_area.append("\nnothing found!");
					    break;
					}
				}
				System.out.println("Read");
			};
		});
	}
}
