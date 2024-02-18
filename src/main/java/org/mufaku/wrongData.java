package org.mufaku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Scanner;

import org.mufaku.utils.javaUtils;

import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class wrongData {
	public static void main(String[] args) throws IOException {

		javaUtils utils = new javaUtils();

		try {
			Path p = Paths.get("G:\\pemilu2024/count-kpu-2024-02-18");

			File myObj = new File("G:\\pemilu2024/kab");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();

				String apiUrl = "https://sirekap-obj-data.kpu.go.id/pemilu/hhcw/ppwp/" + data.substring(0, 2) + "/"
						+ data + ".json";
				ObjectMapper om = new ObjectMapper();

				try {
					String jsonResponse = utils.getJsonFromApi(apiUrl);
					JsonNode dt = om.readValue(jsonResponse, JsonNode.class);
					JsonNode a = dt.get("table");
					Map<String, Object> aa = om.readValue(a.toString(), Map.class);

					int anies = 0;
					int prab = 0;
					int ganjar = 0;
					for (Entry<String, Object> m : aa.entrySet()) {

						String kab = utils.getJsonFromApi(apiUrl.replace(".json", "/" + m.getKey() + ".json"));
						JsonNode dt2 = om.readValue(kab, JsonNode.class);
						JsonNode a2 = dt2.get("table");
						Map<String, Object> aa2 = om.readValue(a2.toString(), Map.class);
						for (Entry<String, Object> m2 : aa2.entrySet()) {
							String kec = utils.getJsonFromApi(
									apiUrl.replace(".json", "/" + m.getKey() + "/" + m2.getKey() + ".json"));
							JsonNode dt3 = om.readValue(kec, JsonNode.class);
							JsonNode a3 = dt3.get("table");
							int i = 0;
							for (JsonNode j : a3) {
								i++;
								try {
									anies += j.get("100025").asInt();
									prab += j.get("100026").asInt();
									ganjar += j.get("100027").asInt();
									if (j.get("100025").asInt() + j.get("100026").asInt()
											+ j.get("100027").asInt() > 400) {
										String link = apiUrl.replace(".json",
												"/" + m.getKey() + "/" + m2.getKey() + "");
										String link_pemilu = link.replace(
												"https://sirekap-obj-data.kpu.go.id/pemilu/hhcw/ppwp/",
												"https://pemilu2024.kpu.go.id/pilpres/hitung-suara/");
										String tps = "000";
										if (i >= 10) {
											tps = "0" + i;
										} else {
											tps = "00" + i;
										}
										if (i >= 100) {
											tps = i + "";
										}
										String[] no_des_tps = link_pemilu.split("/");
										System.out.println(link_pemilu + "/" + no_des_tps[no_des_tps.length - 1] + tps);

										String s = j.get("100025").asInt() + "\t" + j.get("100026").asInt() + "\t"
												+ j.get("100027").asInt() + "\t" + link_pemilu + "/"
												+ no_des_tps[no_des_tps.length - 1] + tps;
										s += "\n";
										try {
											Files.write(p, s.getBytes(), StandardOpenOption.APPEND);
										} catch (IOException e) {
											System.err.println(e);
										}
									}
								} catch (Exception e) {
								}
							}

						}

					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

}
