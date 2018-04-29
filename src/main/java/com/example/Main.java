/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import static javax.measure.unit.SI.KILOGRAM;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.measure.quantity.Mass;
import javax.sql.DataSource;

import org.jscience.physics.amount.Amount;
import org.jscience.physics.model.RelativisticModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Controller
@SpringBootApplication
public class Main 
{	
	@RequestMapping("/hello")
	String hello(Map<String, Object> model)
	{
	    RelativisticModel.select();
	    String energy = System.getenv().get("ENERGY");
		if (energy == null) {
	       energy = "12 GeV";
	    }
	    Amount<Mass> m = Amount.valueOf(energy).to(KILOGRAM);
		model.put("science", "Yo Ho Ho!"/* "E=mc^2: " + energy + " = " + m.toString() */);
	    return "hello";
	}

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) throws Exception 
	{
		SpringApplication.run(Main.class, args);
	}

	@RequestMapping("/")
	String index() 
	{
		return "index";
	}

	@RequestMapping("/db")
	String db(Map<String, Object> model) 
	{
		try (Connection connection = dataSource.getConnection()) 
		{
			Statement stmt = connection.createStatement();
			// stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
			// stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
			// ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");
			//
			// ArrayList<String> output = new ArrayList<String>();
			// while (rs.next())
			// {
			// output.add("Read from DB: " + rs.getTimestamp("tick"));
			// }
			//
			// model.put("records", output);
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" 
			+ "userID SERIAL PRIMARY KEY,"
			+ "name text NOT NULL,"
			+ "gender text NOT NULL,"
			+ "dateOfBirth date NOT NULL,"
			+ "CNIC INT NOT NULL,"
			+ "Address text NOT NULL,"
			+ "contactNo text NOT NULL,"
			+ "username text NOT NULL UNIQUE,"
			+ "password text NOT NULL,"
			+ "role text NOT NULL,"
			+ "rating REAL"
			+ ")");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS comments (" 
					+ "reviewID SERIAL PRIMARY KEY,"
					+ "userID INT REFERENCES users(userID) NOT NULL,"
					+ "review text NOT NULL"
					+ ")");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rooms (" 
					+ "roomID SERIAL PRIMARY KEY,"
					+ "price REAL NOT NULL,"
					+ "availability boolean NOT NULL,"
					+ "noOfBeds INT NOT NULL,"
					+ "atCorner boolean NOT NULL,"
					+ "picURL text"
					+ ")");
			stmt.executeUpdate(
					"INSERT INTO users (name, gender, dateOfBirth, CNIC, Address, contactNo, username, password, role, rating) values ('Ahsan', 'male', '22-05-1996', 123456789, 'Amity Park, London', '090078601', 'CodeSage', 'tempest', 'admin', NULL)");
			model.put("records", "admin inserted successfully");
			return "db";
		} 
		catch (Exception e)
		{
			model.put("message", e.getMessage());
			return "error";
		}
	}

	@Bean
	public DataSource dataSource() throws SQLException 
	{
		if (dbUrl == null || dbUrl.isEmpty()) {
			return new HikariDataSource();
		} 
		else 
		{
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(dbUrl);
			return new HikariDataSource(config);
		}
	}
}
