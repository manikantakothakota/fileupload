package com.mani.filedata.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mani.filedata.UserRepo;
import com.mani.filedata.entity.User;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
@Transactional
public class USerService {

	@Autowired
	private UserRepo repo;

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	public boolean saveDatafromUploadedfile(MultipartFile file) {
		// TODO Auto-generated method stub

		boolean isFlag=false;
		String extension=FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("json")) {
			isFlag=readDataFromJson(file);
		}else if(extension.equalsIgnoreCase("csv")) {
			isFlag=readDataFromCSV(file);
		}else if(extension.equalsIgnoreCase("xlsx")||extension.equalsIgnoreCase("xls")) {
			isFlag=readDataFromExecl(file);
		}

		return isFlag;
	}

	private boolean readDataFromExecl(MultipartFile file) {
		// TODO Auto-generated method stub
		Workbook wb=getWorkBook(file);
		Sheet sheet=wb.getSheetAt(0);
		Iterator<Row> rows=sheet.iterator();
		rows.next();
		while(rows.hasNext()) {
			Row row=rows.next();
			User user=new User();
			if(row.getCell(0).getCellType()==CellType.STRING) {
				user.setFirstName(row.getCell(0).getStringCellValue());
			}
			if(row.getCell(1).getCellType()==CellType.STRING) {
				user.setLastName(row.getCell(1).getStringCellValue());
			}
			if(row.getCell(2).getCellType()==CellType.STRING) {
				user.setEmail(row.getCell(2).getStringCellValue());
			}
			if(row.getCell(3).getCellType()==CellType.NUMERIC) {
				String phone=NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
				user.setPhone(phone);
			}else if(row.getCell(3).getCellType()==CellType.STRING) {
				user.setPhone(row.getCell(3).getStringCellValue());
			}
			user.setFiletype(FilenameUtils.getExtension(file.getOriginalFilename()));
			repo.save(user);

		}
		return true;
	}

	private Workbook getWorkBook(MultipartFile file) {
		// TODO Auto-generated method stub
		Workbook wb=null;
		String extension=FilenameUtils.getExtension(file.getOriginalFilename());
		try {
			if (extension.equalsIgnoreCase("xlsx")) {
				wb=new XSSFWorkbook(file.getInputStream());
			}else if (extension.equalsIgnoreCase("xls")) {
				wb=new HSSFWorkbook(file.getInputStream());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return wb;
	}

	private boolean readDataFromCSV(MultipartFile file) {
		// TODO Auto-generated method stub

		try {
			InputStreamReader isr=new InputStreamReader(file.getInputStream());
			CSVReader csv=new CSVReaderBuilder(isr).withSkipLines(1).build();
			List<String[]> rows=csv.readAll();
			for(String[] row:rows) {
				
				User user=new User();
				user.setFirstName(row[0]);
				user.setLastName(row[1]);
				user.setEmail(row[2]);
				user.setPhone(row[3]);
				user.setFiletype(FilenameUtils.getExtension(file.getOriginalFilename()));
				repo.save(user);
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	private boolean readDataFromJson(MultipartFile file) {
		// TODO Auto-generated method stub


		try {
			InputStream is= file.getInputStream();
			ObjectMapper mapper=new ObjectMapper();
			List<User> users=Arrays.asList(mapper.readValue(is, User[].class));

			if(users!=null && users.size()>0) {
				for(User user:users) {
					user.setFiletype(FilenameUtils.getExtension(file.getOriginalFilename()));
					repo.save(user);
				}
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

}
