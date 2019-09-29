package com.amazonaws.samples;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;


/**
 * Hello world!
 *
 */
public class Test 
{
	static String url;
	static AmazonSQS sqs;
	private static String messages;
	private static Scanner input;
	private static int choice;
	public static void Connect()
	{
		sqs =AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}
	public static void Create()
	{
		String QueueName = null;
		System.out.println("Please input the name of the queue you create:");
		input = new Scanner(System.in);
		QueueName=input.nextLine();
		url=sqs.createQueue(new CreateQueueRequest(QueueName)).getQueueUrl();
	}
	public static void Send(String message)
	{
		System.out.println("Please input the message you want to send:");
		input = new Scanner(System.in);
        messages=input.nextLine();
		sqs.sendMessage(new SendMessageRequest(url,messages));
	}
	public static void Read()
	{
		while (true) 
		{
			List<Message> msgs = sqs.receiveMessage(
			     new ReceiveMessageRequest(url).withMaxNumberOfMessages(1)).getMessages();
			if (msgs.size() > 0) 
			{
			   Message message = msgs.get(0);
			   System.out.println("The message is " + message.getBody());
			   sqs.deleteMessage(new DeleteMessageRequest(url, message.getReceiptHandle()));
			} 
			else 
			{
			    System.out.println("nothing found!");
			    break;
			}
		}
	}
    public static void main( String[] args ) throws IOException
    {
    	Connect();
    	System.out.println("Please choose 1 or 2 or 3:\n1.create a queue;\n2.use the old queue;\n3.close");
    	input = new Scanner(System.in);
		choice=input.nextInt();
		if(choice==1)
			Create();
		else if(choice==2)
		{
			System.out.println("Please input the url of the old queue:");
			input = new Scanner(System.in);
	    	url=input.nextLine();
		}
		else if(choice==3)
			return;
		else
			System.out.println("Wrong input!");
		while(true)
		{
			System.out.println("Please choose 1 or 2 or 3:\n1.send a message;\n2.read the messages;\n3.close");
			input = new Scanner(System.in);
			choice=input.nextInt();
			if(choice==1)
			{
    	        Send(messages);
			}
			else if(choice==2)
				Read();
			else if(choice==3)
				break;
			else
				System.out.println("Wrong input!");
		}
		return;
    }
}
