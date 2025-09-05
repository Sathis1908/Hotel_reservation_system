package com.jspiders.hrs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MainOfHRS2 {
	private static final String dburl="jdbc:mysql://localhost:3306/hotel_db";
	private static final String user="root";
	private static final String password="root";
	public static void main(String[] args) {
		
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		try {
			Connection con=DriverManager.getConnection(dburl,user,password);
			Scanner s=new Scanner(System.in);
			while(true)
			{
			System.out.println("Welcome To Hotel Reservation System");
			System.out.println("1.New Reservation\n2.View Reservation\n3.Update Reservation\n4.Get Room Numbers\n5.Delete Reservations\n0.Exit\nChoose an Option :");
			int choice=s.nextInt();
			
			
			switch(choice)
			{
				case 1:
				{
					reservationRoom(con,s);
					break;
				}
				case 2:
				{
					viewReservations(con);
					break;
				}
				case 3:
				{
					updateReservations(con, s);
					break;
				}
				case 4:
				{
					getRoomNo(con, s);
					break;
				}
				case 5:
				{
					deleteReservations(con, s);
					break;
				}
				case 0:
				{
					exit();
					s.close();
					return;
				}
				default: System.out.println("Invalid Choice... Try Again.");
			}
			
		}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
	}
	//New Reservation
	public static void reservationRoom(Connection con,Scanner sc)
	{
		try {
			System.out.println("Enter Guest Name:");
			String name=sc.next();

			System.out.println("Enter Guest phone Number:");
			String phone=sc.next();

			System.out.println("Enter Room Number:");
			int room=sc.nextInt();
		
			String query="INSERT INTO RESERVATIONS (GUEST_NAME,GUEST_NUMBER,ROOM_NUMBER) VALUES(?,?,?)";
		
			try(PreparedStatement ps=con.prepareStatement(query)) {
				ps.setString(1, name);
				ps.setString(2, phone);
				ps.setInt(3, room);
				int count=ps.executeUpdate();
				if(count>0)
				{
					System.out.println("Reservation Successfully");
				}
				else
				{
					System.out.println("Reservation Failed");
				}
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	
	//method for view reservation
	
	private static void viewReservations(Connection con)
	{
		String query="SELECT * FROM RESERVATIONS";
		try
		{
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(query);
			
			System.out.println("+---------+---------------------------Current Reservation List-+--------------------+--------------------+");
			System.out.println("|  R_ID   |         Guest_Name          |     Guest_Contact    |     Room_Number    |       R_Date       |");
			System.out.println("+---------+-----------------------------+----------------------+--------------------+--------------------+");
			
			while(rs.next())
			{
				int id=rs.getInt(1);
				String gname=rs.getString(2);
				String phone=rs.getString(3);
				int roomno=rs.getInt(4);
				String date=rs.getTimestamp(5).toString();
				
				System.out.printf("| %-14d | %-15s | %-20s | %-13d | %-19s |\n",id,gname,phone,roomno,date);
			}
			System.out.println("+---------+-----------------------------+----------------------+--------------------+--------------------+");
			
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Method for get reservation room number
	private static void getRoomNo(Connection con,Scanner sc)
	{
		try {
			System.out.println("Enter Reservation Id : ");
			int id=sc.nextInt();
			System.out.println("Enter Guest Name : ");
			String gname=sc.next();
			
			String query = "SELECT ROOM_NUMBER FROM RESERVATIONS WHERE R_ID= ? AND GUEST_NAME = ?";
			try(PreparedStatement ps=con.prepareStatement(query))
			{
				ps.setInt(1, id);
				ps.setString(2, gname);
				ResultSet rs=ps.executeQuery();
				if(rs.next())
				{
				int room=rs.getInt(1);
				
				System.out.println("Room Number for given Reservation Id: "+id+" Guest Name: "+gname+" = "+room);
				}
				else
				{
					System.out.println("Reservation Not Found for  given Reservation Id: "+id+" Guest Name: "+gname);
				}
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//method for Update Reservation
	
	private static void updateReservations(Connection con,Scanner sc)
	{
		try {
			System.out.println("Enter Reservation id to update :");
			int id=sc.nextInt();
			
			if(!reservationExists(con,id))
			{
				System.out.println("Reservation not Found for given ID.");
				return;
			}
			System.out.println("Enter New Guest Name : ");
			String name=sc.next();

			System.out.println("Enter New Contact Number : ");
			String contact=sc.next();

			System.out.println("Enter New Room Number : ");
			int room=sc.nextInt();
			
			String query = "UPDATE RESERVATIONS SET GUEST_NAME= ?, GUEST_NUMBER = ? , ROOM_NUMBER = ? WHERE R_ID=?";
			try(PreparedStatement ps=con.prepareStatement(query))
			{
				ps.setString(1, name);
				ps.setString(2, contact);
				ps.setInt(3, room);
				ps.setInt(4, id);
				int count=ps.executeUpdate();
				if(count>0)
				{
					System.out.println("Reservation Updated Successfully");
					
				}
				else
				{
					System.out.println("Reservation Updation Failed");
				}
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//method for checking Reservation is exist or not
	private static boolean reservationExists(Connection con, int id) {
		try {
			String query="SELECT R_ID FROM RESERVATIONS WHERE R_ID=?";
			
			try(PreparedStatement  ps=con.prepareStatement(query))
			{
				ps.setInt(1, id);
				ResultSet rs=ps.executeQuery();
				return rs.next();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	//method for delete the reservation

	private static void deleteReservations(Connection con, Scanner sc)
	{
		try {
			System.out.println("Enter Reservation id to delete :");
			int id=sc.nextInt();
			if(!reservationExists(con, id))
			{
				System.out.println("Reservation not found for given ID to Delete.");
			}
			
			String query="DELETE FROM RESERVATIONS WHERE R_ID= ?";
			try(PreparedStatement ps=con.prepareStatement(query))
			{
				ps.setInt(1, id);
				int count=ps.executeUpdate();
				if(count>0)
				{
					System.out.println("Reservation deleted Successfully");
				}
				else
				{
					System.out.println("Reservation deleted Failed");
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//method for exit
	public static void exit() throws InterruptedException {
		System.out.println("Exiting System");
		int i=5;
		while(i!=0)
		{
			System.out.print(".");
			Thread.sleep(1000);
			i--;
		}
		System.out.println();
		System.out.println("Thank you Using Hotel Reservation System!!!"); 
	}
}
