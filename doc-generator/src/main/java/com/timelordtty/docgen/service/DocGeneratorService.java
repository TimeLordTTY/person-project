package com.timelordtty.docgen.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.timelordtty.docgen.utils.JsonPlaceholderProcessor;

/**
 * 文档生成服务类，处理基于Word和Excel模板生成文档的核心逻辑
 */
public class DocGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(DocGeneratorService.class);
    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    
    private final ObjectMapper objectMapper;
    
    /**
     * 构造函数
     */
    public DocGeneratorService() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 生成文档的主方法
     * 
     * @param templatePath 模板文件路径
     * @param outputDir 输出目录
     * @param dataFilePath 数据文件路径
     * @param isWordTemplate 是否为Word模板
     * @return 生成的文件路径
     * @throws Exception 处理过程中的异常
     */
    public String generateDocument(String templatePath, String outputDir, String dataFilePath, boolean isWordTemplate) throws Exception {
        // 验证文件是否存在
        File templateFile = new File(templatePath);
        File dataFile = new File(dataFilePath);
        File outputDirFile = new File(outputDir);
        
        if (!templateFile.exists()) {
            throw new FileNotFoundException("模板文件不存在: " + templatePath);
        }
        
        if (!dataFile.exists()) {
            throw new FileNotFoundException("数据文件不存在: " + dataFilePath);
        }
        
        if (!outputDirFile.exists()) {
            if (!outputDirFile.mkdirs()) {
                throw new IOException("无法创建输出目录: " + outputDir);
            }
        }
        
        // 读取数据文件内容
        Map<String, Object> dataMap = readDataFile(dataFilePath);
        logger.info("已读取数据: {} 条记录", dataMap.size());
        
        // 生成输出文件名（添加时间戳）
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path templatePath1 = Paths.get(templatePath);
        String fileName = templatePath1.getFileName().toString();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String outputFileName = baseName + "_" + timestamp + extension;
        String outputFilePath = Paths.get(outputDir, outputFileName).toString();
        
        // 根据文件类型调用相应的处理方法
        if (isWordTemplate) {
            generateWordDocument(templatePath, outputFilePath, dataMap);
        } else {
            generateExcelDocument(templatePath, outputFilePath, dataMap);
        }
        
        logger.info("文档生成成功: {}", outputFilePath);
        return outputFilePath;
    }
    
    /**
     * 读取JSON数据文件
     * 
     * @param dataFilePath 数据文件路径
     * @return 包含数据的Map对象
     * @throws IOException IO异常
     */
    private Map<String, Object> readDataFile(String dataFilePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(dataFilePath)) {
            return objectMapper.readValue(fis, new TypeReference<Map<String, Object>>() {});
        }
    }
    
    /**
     * 生成Word文档
     * 
     * @param templatePath 模板文件路径
     * @param outputPath 输出文件路径
     * @param dataMap 数据映射
     * @throws IOException IO异常
     */
    private void generateWordDocument(String templatePath, String outputPath, Map<String, Object> dataMap) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            // 处理文档中的段落和表格
            processParagraphs(document.getParagraphs(), dataMap);
            processTables(document.getTables(), dataMap);
            
            // 保存生成的文档
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }
        }
    }
    
    /**
     * 处理Word文档中的段落
     * 
     * @param paragraphs 段落列表
     * @param dataMap 数据映射
     */
    private void processParagraphs(List<XWPFParagraph> paragraphs, Map<String, Object> dataMap) {
        for (XWPFParagraph paragraph : paragraphs) {
            String paragraphText = paragraph.getText();
            
            if (paragraphText != null && paragraphText.contains(PLACEHOLDER_PREFIX)) {
                logger.debug("处理包含占位符的段落: {}", paragraphText);
                
                // 创建替换后的文本
                String processedText = JsonPlaceholderProcessor.processPlaceholders(paragraphText, dataMap);
                
                if (!processedText.equals(paragraphText)) {
                    logger.debug("替换后的文本: {}", processedText);
                    
                    // 清除现有的runs
                    int runsCount = paragraph.getRuns().size();
                    for (int i = runsCount - 1; i >= 0; i--) {
                        paragraph.removeRun(i);
                    }
                    
                    // 添加新的run，包含替换后的文本
                    XWPFRun newRun = paragraph.createRun();
                    newRun.setText(processedText, 0);
                    
                    // 尝试保留原格式（加粗、斜体等）
                    // 这里简化处理，实际使用可能需要更复杂的格式保留逻辑
                    try {
                        if (runsCount > 0 && paragraph.getRuns().get(0) != null) {
                            XWPFRun originalRun = paragraph.getRuns().get(0);
                            newRun.setBold(originalRun.isBold());
                            newRun.setItalic(originalRun.isItalic());
                            newRun.setFontFamily(originalRun.getFontFamily());
                            if (originalRun.getFontSize() > 0) {
                                newRun.setFontSize(originalRun.getFontSize());
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("复制格式时出错: {}", e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * 处理Word文档中的表格
     * 
     * @param tables 表格列表
     * @param dataMap 数据映射
     */
    private void processTables(List<XWPFTable> tables, Map<String, Object> dataMap) {
        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    processParagraphs(cell.getParagraphs(), dataMap);
                }
            }
        }
    }
    
    /**
     * 生成Excel文档
     * 
     * @param templatePath 模板文件路径
     * @param outputPath 输出文件路径
     * @param dataMap 数据映射
     * @throws IOException IO异常
     */
    private void generateExcelDocument(String templatePath, String outputPath, Map<String, Object> dataMap) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            
            // 遍历工作簿中的所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                processSheet(sheet, dataMap);
            }
            
            // 保存生成的Excel文件
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
        }
    }
    
    /**
     * 处理Excel工作表
     * 
     * @param sheet 工作表
     * @param dataMap 数据映射
     */
    private void processSheet(Sheet sheet, Map<String, Object> dataMap) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    String cellValue = cell.getStringCellValue();
                    
                    // 检查单元格是否包含占位符
                    if (cellValue.contains(PLACEHOLDER_PREFIX)) {
                        // 处理单元格内的所有占位符
                        String processedValue = JsonPlaceholderProcessor.processPlaceholders(cellValue, dataMap);
                        
                        // 更新单元格值
                        cell.setCellValue(processedValue);
                        
                        // 如果处理后的值是纯数字或日期，尝试转换单元格类型
                        try {
                            if (processedValue.matches("^\\d+$")) {
                                // 整数
                                cell.setCellValue(Long.parseLong(processedValue));
                            } else if (processedValue.matches("^\\d+\\.\\d+$")) {
                                // 小数
                                cell.setCellValue(Double.parseDouble(processedValue));
                            }
                        } catch (NumberFormatException e) {
                            // 忽略转换错误，保持为字符串
                            logger.debug("无法将值转换为数字: {}", processedValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * 用于测试的主方法
     * 
     * @param args 命令行参数，"word"表示测试Word文档，"excel"表示测试Excel文档
     */
    public static void main(String[] args) {
        boolean isWordTemplate = true;
        
        // 解析命令行参数
        if (args.length > 0 && "excel".equalsIgnoreCase(args[0])) {
            isWordTemplate = false;
        }
        
        try {
            // 设置测试模板和数据文件路径
            String templatePath = isWordTemplate 
                ? "doc-generator/src/main/resources/templates/project_report.docx"
                : "doc-generator/src/main/resources/templates/project_status.xlsx";
            
            String outputDir = "doc-generator/target/test-output";
            String dataFilePath = "doc-generator/src/main/resources/templates/sample_data.json";
            
            // 创建输出目录（如果不存在）
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }
            
            // 检查文件是否存在
            File templateFile = new File(templatePath);
            File dataFile = new File(dataFilePath);
            
            if (!templateFile.exists()) {
                System.err.println("错误: 模板文件不存在: " + templatePath);
                return;
            }
            
            if (!dataFile.exists()) {
                System.err.println("错误: 数据文件不存在: " + dataFilePath);
                return;
            }
            
            System.out.println("测试参数:");
            System.out.println("- 模板类型: " + (isWordTemplate ? "Word" : "Excel"));
            System.out.println("- 模板文件: " + templatePath);
            System.out.println("- 数据文件: " + dataFilePath);
            System.out.println("- 输出目录: " + outputDir);
            
            // 创建服务实例并调用生成方法
            DocGeneratorService service = new DocGeneratorService();
            String outputPath = service.generateDocument(templatePath, outputDir, dataFilePath, isWordTemplate);
            
            System.out.println("文档生成成功: " + outputPath);
        } catch (Exception e) {
            System.err.println("文档生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 