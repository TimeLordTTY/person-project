package com.timelordtty.docgen.utils;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 生成示例Excel电子表格模板
 */
public class ExcelTemplateGenerator {

    public static void main(String[] args) {
        try {
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            
            // 创建项目信息工作表
            Sheet projectSheet = workbook.createSheet("项目信息");
            createProjectInfoSheet(workbook, projectSheet);
            
            // 创建里程碑工作表
            Sheet milestoneSheet = workbook.createSheet("里程碑");
            createMilestoneSheet(workbook, milestoneSheet);
            
            // 创建资源工作表
            Sheet resourceSheet = workbook.createSheet("资源分配");
            createResourceSheet(workbook, resourceSheet);
            
            // 创建风险工作表
            Sheet riskSheet = workbook.createSheet("风险管理");
            createRiskSheet(workbook, riskSheet);
            
            // 创建总结工作表
            Sheet summarySheet = workbook.createSheet("项目总结");
            createSummarySheet(workbook, summarySheet);
            
            // 自动调整列宽
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (int j = 0; j < 10; j++) {
                    sheet.autoSizeColumn(j);
                }
            }
            
            // 保存工作簿
            FileOutputStream fileOut = new FileOutputStream("doc-generator/src/main/resources/templates/project_status.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            
            // 关闭工作簿
            workbook.close();
            
            System.out.println("成功创建Excel模板文件: project_status.xlsx");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 创建项目信息工作表
     */
    private static void createProjectInfoSheet(Workbook workbook, Sheet sheet) {
        // 创建标题样式
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("${reportInfo.title}");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        // 报告基本信息
        Row infoRow1 = sheet.createRow(1);
        Cell infoLabelCell1 = infoRow1.createCell(0);
        infoLabelCell1.setCellValue("报告编号:");
        infoLabelCell1.setCellStyle(headerStyle);
        
        Cell infoValueCell1 = infoRow1.createCell(1);
        infoValueCell1.setCellValue("${reportInfo.reportId}");
        infoValueCell1.setCellStyle(dataStyle);
        
        Row infoRow2 = sheet.createRow(2);
        Cell infoLabelCell2 = infoRow2.createCell(0);
        infoLabelCell2.setCellValue("作者:");
        infoLabelCell2.setCellStyle(headerStyle);
        
        Cell infoValueCell2 = infoRow2.createCell(1);
        infoValueCell2.setCellValue("${reportInfo.author}");
        infoValueCell2.setCellStyle(dataStyle);
        
        Row infoRow3 = sheet.createRow(3);
        Cell infoLabelCell3 = infoRow3.createCell(0);
        infoLabelCell3.setCellValue("部门:");
        infoLabelCell3.setCellStyle(headerStyle);
        
        Cell infoValueCell3 = infoRow3.createCell(1);
        infoValueCell3.setCellValue("${reportInfo.department}");
        infoValueCell3.setCellStyle(dataStyle);
        
        Row infoRow4 = sheet.createRow(4);
        Cell infoLabelCell4 = infoRow4.createCell(0);
        infoLabelCell4.setCellValue("日期:");
        infoLabelCell4.setCellStyle(headerStyle);
        
        Cell infoValueCell4 = infoRow4.createCell(1);
        infoValueCell4.setCellValue("${reportInfo.date}");
        infoValueCell4.setCellStyle(dataStyle);
        
        // 空行
        sheet.createRow(5);
        
        // 项目信息标题
        Row projectTitleRow = sheet.createRow(6);
        Cell projectTitleCell = projectTitleRow.createCell(0);
        projectTitleCell.setCellValue("项目基本信息");
        projectTitleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 3));
        
        // 项目详细信息
        Row projectRow1 = sheet.createRow(7);
        Cell projectLabelCell1 = projectRow1.createCell(0);
        projectLabelCell1.setCellValue("项目名称:");
        projectLabelCell1.setCellStyle(headerStyle);
        
        Cell projectValueCell1 = projectRow1.createCell(1);
        projectValueCell1.setCellValue("${projectInfo.name}");
        projectValueCell1.setCellStyle(dataStyle);
        
        Row projectRow2 = sheet.createRow(8);
        Cell projectLabelCell2 = projectRow2.createCell(0);
        projectLabelCell2.setCellValue("项目负责人:");
        projectLabelCell2.setCellStyle(headerStyle);
        
        Cell projectValueCell2 = projectRow2.createCell(1);
        projectValueCell2.setCellValue("${projectInfo.manager}");
        projectValueCell2.setCellStyle(dataStyle);
        
        Row projectRow3 = sheet.createRow(9);
        Cell projectLabelCell3 = projectRow3.createCell(0);
        projectLabelCell3.setCellValue("开始日期:");
        projectLabelCell3.setCellStyle(headerStyle);
        
        Cell projectValueCell3 = projectRow3.createCell(1);
        projectValueCell3.setCellValue("${projectInfo.startDate}");
        projectValueCell3.setCellStyle(dataStyle);
        
        Row projectRow4 = sheet.createRow(10);
        Cell projectLabelCell4 = projectRow4.createCell(0);
        projectLabelCell4.setCellValue("结束日期:");
        projectLabelCell4.setCellStyle(headerStyle);
        
        Cell projectValueCell4 = projectRow4.createCell(1);
        projectValueCell4.setCellValue("${projectInfo.endDate}");
        projectValueCell4.setCellStyle(dataStyle);
        
        Row projectRow5 = sheet.createRow(11);
        Cell projectLabelCell5 = projectRow5.createCell(0);
        projectLabelCell5.setCellValue("当前状态:");
        projectLabelCell5.setCellStyle(headerStyle);
        
        Cell projectValueCell5 = projectRow5.createCell(1);
        projectValueCell5.setCellValue("${projectInfo.status}");
        projectValueCell5.setCellStyle(dataStyle);
    }
    
    /**
     * 创建里程碑工作表
     */
    private static void createMilestoneSheet(Workbook workbook, Sheet sheet) {
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("项目里程碑");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        // 表头
        Row headerRow = sheet.createRow(1);
        String[] headers = {"里程碑名称", "开始日期", "结束日期", "状态", "完成度(%)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 里程碑数据
        for (int i = 0; i < 5; i++) {
            Row dataRow = sheet.createRow(i + 2);
            
            Cell nameCell = dataRow.createCell(0);
            nameCell.setCellValue("${milestones[" + i + "].name}");
            nameCell.setCellStyle(dataStyle);
            
            Cell startDateCell = dataRow.createCell(1);
            startDateCell.setCellValue("${milestones[" + i + "].startDate}");
            startDateCell.setCellStyle(dataStyle);
            
            Cell endDateCell = dataRow.createCell(2);
            endDateCell.setCellValue("${milestones[" + i + "].endDate}");
            endDateCell.setCellStyle(dataStyle);
            
            Cell statusCell = dataRow.createCell(3);
            statusCell.setCellValue("${milestones[" + i + "].status}");
            statusCell.setCellStyle(dataStyle);
            
            Cell completionCell = dataRow.createCell(4);
            completionCell.setCellValue("${milestones[" + i + "].completion}");
            completionCell.setCellStyle(dataStyle);
        }
    }
    
    /**
     * 创建资源工作表
     */
    private static void createResourceSheet(Workbook workbook, Sheet sheet) {
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("项目人员资源");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        
        // 表头
        Row headerRow = sheet.createRow(1);
        String[] headers = {"姓名", "职责", "分配比例(%)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 资源数据
        for (int i = 0; i < 4; i++) {
            Row dataRow = sheet.createRow(i + 2);
            
            Cell nameCell = dataRow.createCell(0);
            nameCell.setCellValue("${resources[" + i + "].name}");
            nameCell.setCellStyle(dataStyle);
            
            Cell roleCell = dataRow.createCell(1);
            roleCell.setCellValue("${resources[" + i + "].role}");
            roleCell.setCellStyle(dataStyle);
            
            Cell allocationCell = dataRow.createCell(2);
            allocationCell.setCellValue("${resources[" + i + "].allocation}");
            allocationCell.setCellStyle(dataStyle);
        }
    }
    
    /**
     * 创建风险工作表
     */
    private static void createRiskSheet(Workbook workbook, Sheet sheet) {
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("项目风险");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        // 表头
        Row headerRow = sheet.createRow(1);
        String[] headers = {"风险描述", "影响程度", "发生概率", "缓解计划"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 风险数据
        for (int i = 0; i < 2; i++) {
            Row dataRow = sheet.createRow(i + 2);
            
            Cell descCell = dataRow.createCell(0);
            descCell.setCellValue("${risks[" + i + "].description}");
            descCell.setCellStyle(dataStyle);
            
            Cell impactCell = dataRow.createCell(1);
            impactCell.setCellValue("${risks[" + i + "].impact}");
            impactCell.setCellStyle(dataStyle);
            
            Cell probCell = dataRow.createCell(2);
            probCell.setCellValue("${risks[" + i + "].probability}");
            probCell.setCellStyle(dataStyle);
            
            Cell mitigationCell = dataRow.createCell(3);
            mitigationCell.setCellValue("${risks[" + i + "].mitigationPlan}");
            mitigationCell.setCellStyle(dataStyle);
        }
    }
    
    /**
     * 创建总结工作表
     */
    private static void createSummarySheet(Workbook workbook, Sheet sheet) {
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("项目总结");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // 进度和预算
        Row progressRow = sheet.createRow(1);
        Cell progressLabelCell = progressRow.createCell(0);
        progressLabelCell.setCellValue("项目进度(%):");
        progressLabelCell.setCellStyle(headerStyle);
        
        Cell progressValueCell = progressRow.createCell(1);
        progressValueCell.setCellValue("${summary.progressPercent}");
        progressValueCell.setCellStyle(dataStyle);
        
        Row budgetRow = sheet.createRow(2);
        Cell budgetLabelCell = budgetRow.createCell(0);
        budgetLabelCell.setCellValue("预算使用(%):");
        budgetLabelCell.setCellStyle(headerStyle);
        
        Cell budgetValueCell = budgetRow.createCell(1);
        budgetValueCell.setCellValue("${summary.budgetUsedPercent}");
        budgetValueCell.setCellStyle(dataStyle);
        
        // 空行
        sheet.createRow(3);
        
        // 主要成就
        Row achievementTitleRow = sheet.createRow(4);
        Cell achievementTitleCell = achievementTitleRow.createCell(0);
        achievementTitleCell.setCellValue("主要成就:");
        achievementTitleCell.setCellStyle(headerStyle);
        
        Row achievementRow = sheet.createRow(5);
        Cell achievementCell = achievementRow.createCell(0);
        achievementCell.setCellValue("${summary.keyAchievements}");
        achievementCell.setCellStyle(dataStyle);
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 3));
        
        // 空行
        sheet.createRow(6);
        
        // 面临挑战
        Row challengeTitleRow = sheet.createRow(7);
        Cell challengeTitleCell = challengeTitleRow.createCell(0);
        challengeTitleCell.setCellValue("面临挑战:");
        challengeTitleCell.setCellStyle(headerStyle);
        
        Row challengeRow = sheet.createRow(8);
        Cell challengeCell = challengeRow.createCell(0);
        challengeCell.setCellValue("${summary.challenges}");
        challengeCell.setCellStyle(dataStyle);
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 3));
        
        // 空行
        sheet.createRow(9);
        
        // 下一步计划
        Row nextStepsTitleRow = sheet.createRow(10);
        Cell nextStepsTitleCell = nextStepsTitleRow.createCell(0);
        nextStepsTitleCell.setCellValue("下一步计划:");
        nextStepsTitleCell.setCellStyle(headerStyle);
        
        Row nextStepsRow = sheet.createRow(11);
        Cell nextStepsCell = nextStepsRow.createCell(0);
        nextStepsCell.setCellValue("${summary.nextSteps}");
        nextStepsCell.setCellStyle(dataStyle);
        sheet.addMergedRegion(new CellRangeAddress(11, 11, 0, 3));
    }
    
    /**
     * 创建标题样式
     */
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
    
    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
    
    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
} 