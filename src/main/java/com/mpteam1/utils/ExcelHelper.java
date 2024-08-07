package com.mpteam1.utils;

import com.mpteam1.entities.Answer;
import com.mpteam1.entities.Question;
import com.mpteam1.entities.Quiz;
import com.mpteam1.entities.enums.EQuestionType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Questions";

    static Map<String, Integer> correctAnswersMap = Map.of("A", 0, "B", 1, "C", 2, "D", 3);

    public static boolean hasExcelFormat(MultipartFile file) {

        return TYPE.equals(file.getContentType());
    }

    public static List<Question> excelToQuestions(MultipartFile file, Quiz quiz) {
        try {
            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Question> questions = new ArrayList<>();

            rows.next();
            while (rows.hasNext()) {
                List<Answer> answers = new ArrayList<>();
                Row currentRow = rows.next();
                String content = currentRow.getCell(0).toString().trim();
                String type = currentRow.getCell(1).toString().trim();
                List<String> correctAnswers = List.of(currentRow.getCell(2).toString().trim().split(","));

                Iterator<Cell> cellsInRow = currentRow.iterator();
                for (int i = 0; i < 3; i++) {
                    cellsInRow.next();
                }

                Question question = Question.builder()
                        .content(content)
                        .type(EQuestionType.valueOf(type))
                        .answers(new ArrayList<>())
                        .quiz(quiz)
                        .build();

                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    Answer answer = Answer.builder()
                            .content(currentCell.toString().trim())
                            .isCorrect(false)
                            .question(question)
                            .build();
                    question.getAnswers().add(answer);
                    answers.add(answer);
                }
                correctAnswers.forEach(correctAnswer -> {
                    answers.get(correctAnswersMap.get(correctAnswer)).setIsCorrect(true);
                });
                questions.add(question);
            }
            workbook.close();
            return questions;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
