package com.timelordtty.docgen.utils;

import java.io.FileOutputStream;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

/**
 * 生成示例Word文档模板
 */
public class DocxTemplateGenerator {

    public static void main(String[] args) {
        try {
            // 创建文档对象
            XWPFDocument document = new XWPFDocument();
            
            // 创建标题样式
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("${reportInfo.title}");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("宋体");
            
            // 创建报告基本信息段落
            XWPFParagraph infoParagraph = document.createParagraph();
            infoParagraph.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun infoRun = infoParagraph.createRun();
            infoRun.setText("报告编号: ${reportInfo.reportId}");
            infoRun.addCarriageReturn();
            infoRun.setText("作者: ${reportInfo.author}");
            infoRun.addCarriageReturn();
            infoRun.setText("部门: ${reportInfo.department}");
            infoRun.addCarriageReturn();
            infoRun.setText("日期: ${reportInfo.date}");
            
            // 创建项目信息标题
            XWPFParagraph projectTitleParagraph = document.createParagraph();
            projectTitleParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun projectTitleRun = projectTitleParagraph.createRun();
            projectTitleRun.setText("项目基本信息");
            projectTitleRun.setBold(true);
            projectTitleRun.setFontSize(14);
            projectTitleRun.addCarriageReturn();
            
            // 创建项目信息表格
            XWPFTable projectTable = document.createTable(5, 2);
            projectTable.setCellMargins(100, 100, 100, 100);
            
            setCellText(projectTable.getRow(0).getCell(0), "项目名称", true);
            setCellText(projectTable.getRow(0).getCell(1), "${projectInfo.name}", false);
            
            setCellText(projectTable.getRow(1).getCell(0), "项目负责人", true);
            setCellText(projectTable.getRow(1).getCell(1), "${projectInfo.manager}", false);
            
            setCellText(projectTable.getRow(2).getCell(0), "开始日期", true);
            setCellText(projectTable.getRow(2).getCell(1), "${projectInfo.startDate}", false);
            
            setCellText(projectTable.getRow(3).getCell(0), "结束日期", true);
            setCellText(projectTable.getRow(3).getCell(1), "${projectInfo.endDate}", false);
            
            setCellText(projectTable.getRow(4).getCell(0), "当前状态", true);
            setCellText(projectTable.getRow(4).getCell(1), "${projectInfo.status}", false);
            
            // 添加项目描述
            XWPFParagraph descParagraph = document.createParagraph();
            descParagraph.setSpacingAfter(200);
            
            // 创建里程碑标题
            XWPFParagraph milestoneTitleParagraph = document.createParagraph();
            XWPFRun milestoneTitleRun = milestoneTitleParagraph.createRun();
            milestoneTitleRun.setText("项目里程碑");
            milestoneTitleRun.setBold(true);
            milestoneTitleRun.setFontSize(14);
            milestoneTitleRun.addCarriageReturn();
            
            // 创建里程碑表格
            XWPFTable milestoneTable = document.createTable(6, 5);
            milestoneTable.setCellMargins(100, 100, 100, 100);
            
            // 表头
            setCellText(milestoneTable.getRow(0).getCell(0), "里程碑名称", true);
            setCellText(milestoneTable.getRow(0).getCell(1), "开始日期", true);
            setCellText(milestoneTable.getRow(0).getCell(2), "结束日期", true);
            setCellText(milestoneTable.getRow(0).getCell(3), "状态", true);
            setCellText(milestoneTable.getRow(0).getCell(4), "完成度(%)", true);
            
            // 里程碑1
            setCellText(milestoneTable.getRow(1).getCell(0), "${milestones[0].name}", false);
            setCellText(milestoneTable.getRow(1).getCell(1), "${milestones[0].startDate}", false);
            setCellText(milestoneTable.getRow(1).getCell(2), "${milestones[0].endDate}", false);
            setCellText(milestoneTable.getRow(1).getCell(3), "${milestones[0].status}", false);
            setCellText(milestoneTable.getRow(1).getCell(4), "${milestones[0].completion}", false);
            
            // 里程碑2
            setCellText(milestoneTable.getRow(2).getCell(0), "${milestones[1].name}", false);
            setCellText(milestoneTable.getRow(2).getCell(1), "${milestones[1].startDate}", false);
            setCellText(milestoneTable.getRow(2).getCell(2), "${milestones[1].endDate}", false);
            setCellText(milestoneTable.getRow(2).getCell(3), "${milestones[1].status}", false);
            setCellText(milestoneTable.getRow(2).getCell(4), "${milestones[1].completion}", false);
            
            // 里程碑3
            setCellText(milestoneTable.getRow(3).getCell(0), "${milestones[2].name}", false);
            setCellText(milestoneTable.getRow(3).getCell(1), "${milestones[2].startDate}", false);
            setCellText(milestoneTable.getRow(3).getCell(2), "${milestones[2].endDate}", false);
            setCellText(milestoneTable.getRow(3).getCell(3), "${milestones[2].status}", false);
            setCellText(milestoneTable.getRow(3).getCell(4), "${milestones[2].completion}", false);
            
            // 里程碑4
            setCellText(milestoneTable.getRow(4).getCell(0), "${milestones[3].name}", false);
            setCellText(milestoneTable.getRow(4).getCell(1), "${milestones[3].startDate}", false);
            setCellText(milestoneTable.getRow(4).getCell(2), "${milestones[3].endDate}", false);
            setCellText(milestoneTable.getRow(4).getCell(3), "${milestones[3].status}", false);
            setCellText(milestoneTable.getRow(4).getCell(4), "${milestones[3].completion}", false);
            
            // 里程碑5
            setCellText(milestoneTable.getRow(5).getCell(0), "${milestones[4].name}", false);
            setCellText(milestoneTable.getRow(5).getCell(1), "${milestones[4].startDate}", false);
            setCellText(milestoneTable.getRow(5).getCell(2), "${milestones[4].endDate}", false);
            setCellText(milestoneTable.getRow(5).getCell(3), "${milestones[4].status}", false);
            setCellText(milestoneTable.getRow(5).getCell(4), "${milestones[4].completion}", false);
            
            // 项目人员资源
            XWPFParagraph resourceTitleParagraph = document.createParagraph();
            resourceTitleParagraph.setSpacingBefore(200);
            XWPFRun resourceTitleRun = resourceTitleParagraph.createRun();
            resourceTitleRun.setText("项目人员资源");
            resourceTitleRun.setBold(true);
            resourceTitleRun.setFontSize(14);
            resourceTitleRun.addCarriageReturn();
            
            // 创建资源表格
            XWPFTable resourceTable = document.createTable(5, 3);
            resourceTable.setCellMargins(100, 100, 100, 100);
            
            // 表头
            setCellText(resourceTable.getRow(0).getCell(0), "姓名", true);
            setCellText(resourceTable.getRow(0).getCell(1), "职责", true);
            setCellText(resourceTable.getRow(0).getCell(2), "分配比例(%)", true);
            
            // 人员1
            setCellText(resourceTable.getRow(1).getCell(0), "${resources[0].name}", false);
            setCellText(resourceTable.getRow(1).getCell(1), "${resources[0].role}", false);
            setCellText(resourceTable.getRow(1).getCell(2), "${resources[0].allocation}", false);
            
            // 人员2
            setCellText(resourceTable.getRow(2).getCell(0), "${resources[1].name}", false);
            setCellText(resourceTable.getRow(2).getCell(1), "${resources[1].role}", false);
            setCellText(resourceTable.getRow(2).getCell(2), "${resources[1].allocation}", false);
            
            // 人员3
            setCellText(resourceTable.getRow(3).getCell(0), "${resources[2].name}", false);
            setCellText(resourceTable.getRow(3).getCell(1), "${resources[2].role}", false);
            setCellText(resourceTable.getRow(3).getCell(2), "${resources[2].allocation}", false);
            
            // 人员4
            setCellText(resourceTable.getRow(4).getCell(0), "${resources[3].name}", false);
            setCellText(resourceTable.getRow(4).getCell(1), "${resources[3].role}", false);
            setCellText(resourceTable.getRow(4).getCell(2), "${resources[3].allocation}", false);
            
            // 风险
            XWPFParagraph riskTitleParagraph = document.createParagraph();
            riskTitleParagraph.setSpacingBefore(200);
            XWPFRun riskTitleRun = riskTitleParagraph.createRun();
            riskTitleRun.setText("项目风险");
            riskTitleRun.setBold(true);
            riskTitleRun.setFontSize(14);
            riskTitleRun.addCarriageReturn();
            
            // 创建风险表格
            XWPFTable riskTable = document.createTable(3, 4);
            riskTable.setCellMargins(100, 100, 100, 100);
            
            // 表头
            setCellText(riskTable.getRow(0).getCell(0), "风险描述", true);
            setCellText(riskTable.getRow(0).getCell(1), "影响程度", true);
            setCellText(riskTable.getRow(0).getCell(2), "发生概率", true);
            setCellText(riskTable.getRow(0).getCell(3), "缓解计划", true);
            
            // 风险1
            setCellText(riskTable.getRow(1).getCell(0), "${risks[0].description}", false);
            setCellText(riskTable.getRow(1).getCell(1), "${risks[0].impact}", false);
            setCellText(riskTable.getRow(1).getCell(2), "${risks[0].probability}", false);
            setCellText(riskTable.getRow(1).getCell(3), "${risks[0].mitigationPlan}", false);
            
            // 风险2
            setCellText(riskTable.getRow(2).getCell(0), "${risks[1].description}", false);
            setCellText(riskTable.getRow(2).getCell(1), "${risks[1].impact}", false);
            setCellText(riskTable.getRow(2).getCell(2), "${risks[1].probability}", false);
            setCellText(riskTable.getRow(2).getCell(3), "${risks[1].mitigationPlan}", false);
            
            // 项目总结
            XWPFParagraph summaryTitleParagraph = document.createParagraph();
            summaryTitleParagraph.setSpacingBefore(200);
            XWPFRun summaryTitleRun = summaryTitleParagraph.createRun();
            summaryTitleRun.setText("项目总结");
            summaryTitleRun.setBold(true);
            summaryTitleRun.setFontSize(14);
            summaryTitleRun.addCarriageReturn();
            
            // 进度和预算
            XWPFParagraph progressParagraph = document.createParagraph();
            XWPFRun progressRun = progressParagraph.createRun();
            progressRun.setText("项目进度: ${summary.progressPercent}%");
            progressRun.addCarriageReturn();
            progressRun.setText("预算使用: ${summary.budgetUsedPercent}%");
            progressRun.addCarriageReturn();
            progressRun.addCarriageReturn();
            
            // 主要成就
            XWPFParagraph achievementParagraph = document.createParagraph();
            XWPFRun achievementRun = achievementParagraph.createRun();
            achievementRun.setText("主要成就:");
            achievementRun.setBold(true);
            achievementRun.addCarriageReturn();
            achievementRun.setText("${summary.keyAchievements}");
            achievementRun.setBold(false);
            achievementRun.addCarriageReturn();
            achievementRun.addCarriageReturn();
            
            // 面临挑战
            XWPFParagraph challengeParagraph = document.createParagraph();
            XWPFRun challengeRun = challengeParagraph.createRun();
            challengeRun.setText("面临挑战:");
            challengeRun.setBold(true);
            challengeRun.addCarriageReturn();
            challengeRun.setText("${summary.challenges}");
            challengeRun.setBold(false);
            challengeRun.addCarriageReturn();
            challengeRun.addCarriageReturn();
            
            // 下一步计划
            XWPFParagraph nextStepsParagraph = document.createParagraph();
            XWPFRun nextStepsRun = nextStepsParagraph.createRun();
            nextStepsRun.setText("下一步计划:");
            nextStepsRun.setBold(true);
            nextStepsRun.addCarriageReturn();
            nextStepsRun.setText("${summary.nextSteps}");
            nextStepsRun.setBold(false);
            
            // 保存文档
            FileOutputStream out = new FileOutputStream("doc-generator/src/main/resources/templates/project_report.docx");
            document.write(out);
            out.close();
            
            System.out.println("成功创建Word模板文件: project_report.docx");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 设置表格单元格文本和样式
     */
    private static void setCellText(XWPFTableCell cell, String text, boolean isHeader) {
        if (cell.getParagraphs().size() == 0) {
            cell.addParagraph();
        }
        
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        
        if (isHeader) {
            run.setBold(true);
            cell.setColor("EEEEEE");
        }
        
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setWidth("auto");
    }
} 