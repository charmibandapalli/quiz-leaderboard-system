import java.net.*;
import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class QuizSolver {

    public static void main(String[] args) throws Exception {

        String regNo = "RA2311026010051";  // keep original

        Set<String> seen = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();

        for (int i = 0; i < 10; i++) {

            int retries = 3;
            boolean success = false;

            while (retries > 0 && !success) {
                try {

                    String urlStr = "https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/messages?regNo="
                            + regNo + "&poll=" + i;

                    HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                    conn.setRequestMethod("GET");

                    int status = conn.getResponseCode();

                    if (status == 200) {

                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));

                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }

                        JSONObject json = new JSONObject(response.toString());
                        JSONArray events = json.getJSONArray("events");

                        int pollIndex = json.getInt("pollIndex");

                        for (int j = 0; j < events.length(); j++) {

                            JSONObject e = events.getJSONObject(j);

                            // 🔥 FINAL CORRECT KEY
                            String key = pollIndex + "_" +
                                         e.getString("roundId") + "_" +
                                         e.getString("participant");

                            if (seen.add(key)) {
                                String participant = e.getString("participant");
                                int score = e.getInt("score");

                                scores.put(participant,
                                        scores.getOrDefault(participant, 0) + score);
                            }
                        }

                        System.out.println("Poll " + i + " done ✅");
                        success = true;

                    } else {
                        throw new IOException("HTTP " + status);
                    }

                } catch (Exception e) {
                    retries--;
                    System.out.println("Retrying poll " + i + "... (" + retries + " left)");
                    Thread.sleep(3000);
                }
            }

            Thread.sleep(5000);
        }

        // 🔽 SORT
        List<Map.Entry<String, Integer>> list = new ArrayList<>(scores.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());

        JSONArray leaderboard = new JSONArray();
        int total = 0;

        System.out.println("\n===== FINAL LEADERBOARD =====");
        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
            total += entry.getValue();

            JSONObject obj = new JSONObject();
            obj.put("participant", entry.getKey());
            obj.put("totalScore", entry.getValue());
            leaderboard.put(obj);
        }

        System.out.println("TOTAL SCORE: " + total);

        JSONObject submit = new JSONObject();
        submit.put("regNo", regNo);
        submit.put("leaderboard", leaderboard);

        HttpURLConnection postConn = (HttpURLConnection) new URL(
                "https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/submit")
                .openConnection();

        postConn.setRequestMethod("POST");
        postConn.setDoOutput(true);
        postConn.setRequestProperty("Content-Type", "application/json");

        OutputStream os = postConn.getOutputStream();
        os.write(submit.toString().getBytes());
        os.close();

        BufferedReader responseReader = new BufferedReader(
                new InputStreamReader(postConn.getInputStream()));

        StringBuilder result = new StringBuilder();
        String line2;

        while ((line2 = responseReader.readLine()) != null) {
            result.append(line2);
        }

        System.out.println("\nSERVER RESPONSE: " + result.toString());
    }
}