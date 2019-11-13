package test;

import java.io.IOException;
import java.util.Scanner;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

@ServerEndpoint(value="/websocket/test")
public class TestEndpoint {
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		MongoClient cli = new MongoClient("localhost");
		MongoDatabase db = cli.getDatabase("human");
		MongoCollection<Document> col = db.getCollection("human");
		System.out.println("来自客户端的消息:" + message);
		Scanner scanner = new Scanner(message);
		String str = scanner.next();
		if (str.equals("find")) {
			String re = "";
			FindIterable<Document> result = col.find();
			MongoCursor<Document> i = result.iterator();
			while (i.hasNext()) {
				Document doc = i.next();
				re += doc.toJson();
				re += "<br>";
			}
			session.getBasicRemote().sendText(re);
		} else {
			Document doc = new Document();
			str = scanner.next();
			doc.put("name", str);
			str = scanner.next();
			doc.put("age", str);
			col.insertOne(doc);
			session.getBasicRemote().sendText("done");
		}
		scanner.close();
		cli.close();
	}
}
