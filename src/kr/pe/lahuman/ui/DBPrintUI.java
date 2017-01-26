package kr.pe.lahuman.ui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kr.pe.lahuman.data.DataMap;
import kr.pe.lahuman.db.DBSelector;
import kr.pe.lahuman.factory.FactoryBean;
import kr.pe.lahuman.image.ImagePath;
import kr.pe.lahuman.out.OutPutFile;
import kr.pe.lahuman.utils.Constants;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DBPrintUI {
	final static Logger log = Logger.getLogger(DBPrintUI.class);

	static {
		String layout = "%d %-5p [%t] %-17c{2} (%13F:%L) %3x - %m%n";
		String logfilename = "DailyLog.log";
		String datePattern = ".yyyy-MM-dd ";

		PatternLayout patternlayout = new PatternLayout(layout);
		DailyRollingFileAppender appender = null;
		ConsoleAppender consoleAppender = null;
		try {
			appender = new DailyRollingFileAppender(patternlayout, logfilename,
					datePattern);
			consoleAppender = new ConsoleAppender(patternlayout);

		} catch (IOException e) {
			log.info(e);
		}
		log.addAppender(appender);
		log.addAppender(consoleAppender);
		log.setLevel(Level.DEBUG);
	}
	
	static Display display = new Display();
	static Shell shell = new Shell(display, SWT.CLOSE);

	public static void main(String[] args) {

		Image img = ImageDescriptor.createFromURL(
				ImagePath.class.getResource("d-print.png")).createImage();
		shell.setImage(img);
		shell.setText("DB-PRINT by lahuman");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Composite wholeComposite = new Composite(shell, SWT.NONE);
		GridLayout layoutWhole = new GridLayout();
		layoutWhole.numColumns = 1;
		wholeComposite.setLayout(layoutWhole);

		String commonGrouptitle = "DBMS";
		int commonColumns = 2;

		final Group commonGroup = makeGroupUI(wholeComposite, commonGrouptitle,
				commonColumns);

		String labelTxt = "DMBS :";
		List<String> comboData = new ArrayList<String>();
		
		comboData.add(Constants.ORACLE);
		comboData.add(Constants.MYSQL);
		int selectIndex = 0;
		final Combo dbmsCombo= makeCombo(commonGroup, labelTxt, comboData, selectIndex);
		final Group dbmsGroup = makeGroupUI(wholeComposite, Constants.ORACLE,
				2);
		final Group outputGroup = makeGroupUI(wholeComposite, "OUTPUT",
				3);
		
		
		makeInput(dbmsGroup, "HOST : ");
		makeInput(dbmsGroup, "PORT : ", "1521");
		makeInput(dbmsGroup, "SID : ");
		makeInput(dbmsGroup, "USER ID : ");
		makeInput(dbmsGroup, "PASSWORD : ");
		makeInput(dbmsGroup, "TABLE LIKE(USE:%test%) :");
		
		dbmsCombo.addSelectionListener(new SelectionAdapter() {
			  public void widgetSelected(SelectionEvent e) {
			        String val = dbmsCombo.getText();
					setVal(dbmsGroup, val);
			      }

			
		});

		List<String> outputList = new ArrayList<String>();
		outputList.add("HTML");
		outputList.add("EXCEL");
		final Combo outputCombo= makeCombo(outputGroup, "OUTPUT TYPE : ",outputList, 0);
		
		
		outputCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				fileBtn.removeSelectionListener(htmlFile);
				fileBtn.removeSelectionListener(excelFile );
				if("HTML".equals(outputCombo.getText())){
					fileBtn.addSelectionListener(htmlFile );			
					((Text) outputGroup.getChildren()[4]).setText("");
				}else{
					fileBtn.addSelectionListener(excelFile );
					((Text) outputGroup.getChildren()[4]).setText("");
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
		Label label = new Label(outputGroup, SWT.NULL);
		makeChoiseFile(outputGroup, "OUTPUT PATH :", "FILE");
		
		// group

		Group btnGroup = new Group(wholeComposite, SWT.NONE);
		org.eclipse.swt.layout.FormLayout layout = new org.eclipse.swt.layout.FormLayout();

		layout.marginLeft = layout.marginRight = 5;
		btnGroup.setLayout(layout);
		btnGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button loadBtn = new Button(btnGroup, SWT.PUSH);
		FormData loadData = new FormData();
		loadData.left = new FormAttachment(0, 0);

		loadBtn.setLayoutData(loadData);
		loadBtn.setText("LOAD CONFIG");
		
		loadBtn.addSelectionListener(loadFile(dbmsCombo, dbmsGroup, outputGroup, outputCombo));
		
		Button saveBtn = new Button(btnGroup, SWT.PUSH);
		FormData saveData = new FormData();
		saveData.left = new FormAttachment(loadBtn, 5);
		saveBtn.setLayoutData(saveData);
		saveBtn.setText("SAVE CONFIG");
		saveBtn.addSelectionListener(saveFile(dbmsCombo, dbmsGroup, outputGroup));
		
		//
		Button makeOutPutBtn = new Button(btnGroup, SWT.PUSH);
		FormData distributData = new FormData();
		distributData.right = new FormAttachment(100);
		makeOutPutBtn.setLayoutData(distributData);
		makeOutPutBtn.setText("MAKE OUTPUT");
		makeOutPutBtn.addSelectionListener(makeOutput(dbmsCombo, dbmsGroup, outputGroup));
		
		shell.open();
		// textUser.forceFocus();

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				// If no more entries in event queue
				display.sleep();
			}
		}

		display.dispose();

	}

	private static SelectionListener makeOutput(final Combo dbmsCombo,
			final Group dbmsGroup, final Group outputGroup) {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM
						| SWT.APPLICATION_MODAL);
				dialog.setLayout(new FillLayout());
				dialog.setSize(400, 100);
				dialog.setText("Result ");

				Composite wholeComposite = new Composite(dialog, SWT.NONE);
				GridLayout layoutWhole = new GridLayout();
				layoutWhole.numColumns = 1;
				wholeComposite.setLayout(layoutWhole);
				Label infoLab = new Label(wholeComposite, SWT.NONE);
				infoLab.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				infoLab.setText("MakeOutPut........");
				final ProgressBar bar = new ProgressBar(wholeComposite,
						SWT.SMOOTH);
				bar.setBounds(10, 10, 200, 20);
				bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				final Button clsBtn = new Button(wholeComposite, SWT.PUSH);
				clsBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				clsBtn.setText("OK");
				clsBtn.setEnabled(false);
				clsBtn.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						dialog.close();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {

					}
				});
				
				
				//validate
				//last table like 제외
				for(int i=0; i<(dbmsGroup.getChildren().length-2); i+=2){
					if(i == 4) continue;
					if(checkStr(((Text) dbmsGroup.getChildren()[(i+1)]).getText(), ((Label) dbmsGroup.getChildren()[i]).getText())){
						return;
					}
				}
				
				
				if(checkStr(((Text) outputGroup.getChildren()[4]).getText(), "OUTPUT PATH")){
					return;
				};
				
				//setValue
				final Map<String, String> dbInfo = new HashMap<String, String>();
				dbInfo.put("serverName", ((Text) dbmsGroup.getChildren()[1]).getText());
				dbInfo.put("serverPort", ((Text) dbmsGroup.getChildren()[3]).getText());
				dbInfo.put("sid", ((Text) dbmsGroup.getChildren()[5]).getText());
				dbInfo.put("userId", ((Text) dbmsGroup.getChildren()[7]).getText());
				dbInfo.put("password", ((Text) dbmsGroup.getChildren()[9]).getText());
				dbInfo.put("tableFilter", ((Text) dbmsGroup.getChildren()[11]).getText()); 
				
				dbInfo.put("dbms", dbmsCombo.getText());
				
				dbInfo.put("outputType", ((Combo) outputGroup.getChildren()[1]).getText());
				dbInfo.put("outputPath", ((Text) outputGroup.getChildren()[4]).getText());
				bar.setMaximum(4);
				
				try{
					new Thread() {
						public void run() {
							
							
							Map<String, List<DataMap<String, String>>> dbData = null;
							int i[] = new int[1];
							
							try {
								DBSelector db = FactoryBean.getInstanceDB(dbInfo.get("dbms"));
								i[0]++;
								barUpdate(bar, clsBtn, i);
								dbData = db.getTableInfos(dbInfo);
								i[0]++;
								barUpdate(bar, clsBtn, i);
								OutPutFile file = FactoryBean.getInstanceFile(dbInfo.get("outputType"));
								i[0]++;
								barUpdate(bar, clsBtn, i);
								file.makeOutput(dbInfo.get("outputPath"), dbData);
								i[0]++;
								barUpdate(bar, clsBtn, i);
								Thread.sleep(1);
							}  catch (Exception e) {
								log.info(e);
								showMessage(e.toString());
								Display.getDefault().syncExec( 
										    new Runnable() { 
										     public void run(){
										    	 clsBtn.setEnabled(true);
										     } 
										    }
										  );
								 return;
							}
						}
						
					}.start();
				}catch(Exception e){
					log.info(e);
				}
				
				
				dialog.open();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		};
	}

	private static SelectionListener saveFile(final Combo dbmsCombo,
			final Group dbmsGroup, final Group outputGroup) {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String[] filterNames = new String[] { "Properties File" };
				String[] filterExtensions = new String[] { "*.properties" };
				FileDialog dialog = makeFileDialog(filterNames, filterExtensions, SWT.SAVE);
				try {
					String propertiesFile  = dialog.open();
					
					Properties prop = new Properties();
					
					prop.setProperty("dbms.type", dbmsCombo.getText());
				
					
					
					prop.setProperty("dbms.host", ((Text) dbmsGroup.getChildren()[1]).getText());
					prop.setProperty("dbms.port", ((Text) dbmsGroup.getChildren()[3]).getText());
					prop.setProperty("dbms.database", ((Text) dbmsGroup.getChildren()[5]).getText());
					prop.setProperty("dbms.user.id", ((Text) dbmsGroup.getChildren()[7]).getText());
					prop.setProperty("dbms.user.password", ((Text) dbmsGroup.getChildren()[9]).getText());
					prop.setProperty("dbms.table.like", ((Text) dbmsGroup.getChildren()[11]).getText());
					
					prop.setProperty("output.type", ((Combo) outputGroup.getChildren()[1]).getText());
					prop.setProperty("output.path", ((Text) outputGroup.getChildren()[4]).getText());
					
		          OutputStream stream = new FileOutputStream(propertiesFile);
		          prop.store(stream, "DBPrint");
		          stream.close();
		     
				}catch(Exception e){
					log.info(e);
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		};
	}

	private static SelectionListener loadFile(final Combo dbmsCombo,
			final Group dbmsGroup, final Group outputGroup,
			final Combo outputCombo) {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				//load file
				String[] filterNames = new String[] { "Properties File" };
				String[] filterExtensions = new String[] { "*.properties" };
				FileDialog dialog = makeFileDialog(filterNames, filterExtensions);
				try {
					String propertiesFile  = dialog.open();
					Properties properties = new Properties();
					FileInputStream file = null;

					try {
						file = new FileInputStream(propertiesFile);
						properties.load(file);
					} catch (IOException e) {
						log.info(e);
					}finally{
						if(file != null)
							file.close();
					}
					
					dbmsCombo.setText(properties.getProperty("dbms.type"));					
					setVal(dbmsGroup, properties.getProperty("dbms.type"));
					((Text) dbmsGroup.getChildren()[1]).setText(properties.getProperty("dbms.host"));
					((Text) dbmsGroup.getChildren()[3]).setText(properties.getProperty("dbms.port"));
					((Text) dbmsGroup.getChildren()[5]).setText(properties.getProperty("dbms.database"));
					((Text) dbmsGroup.getChildren()[7]).setText(properties.getProperty("dbms.user.id"));
					((Text) dbmsGroup.getChildren()[9]).setText(properties.getProperty("dbms.user.password"));
					((Text) dbmsGroup.getChildren()[11]).setText(properties.getProperty("dbms.table.like"));
					
					((Combo) outputGroup.getChildren()[1]).setText(properties.getProperty("output.type"));
					((Text) outputGroup.getChildren()[4]).setText(properties.getProperty("output.path"));
					
					fileBtn.removeSelectionListener(htmlFile);
					fileBtn.removeSelectionListener(excelFile );
					
					if("HTML".equals(outputCombo.getText())){
						fileBtn.addSelectionListener(htmlFile );				
					}else{
						fileBtn.addSelectionListener(excelFile );		
					}
					
				} catch (Exception e) {
					log.info(e);
					if(e instanceof java.lang.IllegalArgumentException)
						showMessage("It's wrong file.");
				}
			}

			
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		};
	}

	private static Combo makeCombo(final Group commonGroup, String labelTxt,
			List<String> comboData, int selectIndex) {
		Label label = new Label(commonGroup, SWT.NULL);
		label.setText(labelTxt);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Combo comboDropDown = new Combo(commonGroup, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		for(String data : comboData){
			comboDropDown.add(data);
		}
		comboDropDown.select(selectIndex);
		
		return comboDropDown ;
	}

	private static Group makeGroupUI(Composite wholeComposite,
			String commonGrouptitle, int commonColumns) {
		final Group commonGroup = new Group(wholeComposite, SWT.NONE);
		commonGroup.setText(commonGrouptitle);
		GridLayout layout = new GridLayout();
		layout.numColumns = commonColumns;
		commonGroup.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		commonGroup.setLayoutData(data);
		return commonGroup;
	}
	
	private static void makeInput(Group group, String string) {
		makeInput(group, string, "");
	}
	private static void makeInput(Group group, String string, String setVal) {
		Label label = new Label(group, SWT.NULL);
		label.setText(string);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Text text = new Text(group, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setText(setVal);
	}
	private static FileDialog makeFileDialog(String[] filterNames, String[] filterExtensions) {
		return makeFileDialog(filterNames, filterExtensions, SWT.OPEN);
	}
	private static FileDialog makeFileDialog(String[] filterNames, String[] filterExtensions, int type) {
		FileDialog dialog = new FileDialog(
				shell, type);
	
		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterPath = "c:\\";
		}
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);
		dialog.setFilterPath(filterPath);
		dialog.setFileName("");
		return dialog;
	}
	
	final public static void setVal(final Group dbmsGroup, String val) {
		if (val.equals(Constants.ORACLE)) {
			dbmsGroup.setText(Constants.ORACLE);
			((Label) dbmsGroup.getChildren()[4]).setText("SID :");
			((Text) dbmsGroup.getChildren()[3]).setText("1521");
		} else if (val.equals(Constants.MYSQL)) {
			dbmsGroup.setText(Constants.MYSQL);
			((Label) dbmsGroup.getChildren()[4]).setText("DATABASE :");
			((Text) dbmsGroup.getChildren()[3]).setText("3306");
		}
	}
	
	static Button fileBtn = null;
	static Text filepathOutput =null; 
	private static void makeChoiseFile(Group commonGroup, String lableText,
			final String btnText) {
		Label label = new Label(commonGroup, SWT.NULL);
		label.setText(lableText);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		filepathOutput = new Text(commonGroup, SWT.SINGLE | SWT.BORDER);
		filepathOutput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filepathOutput.setEnabled(false);
		fileBtn = new Button(commonGroup, SWT.PUSH);
		fileBtn.setText(btnText);
		fileBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fileBtn.addSelectionListener(htmlFile );
	}
	
		static SelectionListener htmlFile = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
				String[] filterNames = new String[] { "HTML File" };
				String[] filterExtensions = new String[] { "*.html" };
				
				FileDialog dialog = makeFileDialog(filterNames, filterExtensions, SWT.SAVE);
				try {
					filepathOutput.setText(dialog.open());
				} catch (Exception e) {
					log.info(e);
				}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			filepathOutput.setText("");
		}
	};
	
	static SelectionListener excelFile = new SelectionListener() {
	@Override
	public void widgetSelected(SelectionEvent arg0) {
		
			String[] filterNames = new String[] { "EXCEL File" };
			String[] filterExtensions = new String[] { "*.xls" };
			
			FileDialog dialog = makeFileDialog(filterNames, filterExtensions, SWT.SAVE);
			try {
				filepathOutput.setText(dialog.open());
			} catch (Exception e) {
				log.info(e);
			}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		filepathOutput.setText("");
	}
};
	
	private static void showMessage(final String message) {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				final Shell dialogMsg =
				          new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				        dialogMsg.setLayout(new GridLayout());
				        dialogMsg.setText("ERROR MASSAGE!");
				        Label msg = new Label(dialogMsg, SWT.NULL);
						msg.setText(message);
				        msg.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
				        
				        Button okButton = new Button(dialogMsg, SWT.PUSH);
				        okButton.setText("OK");
				        okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				        okButton.addSelectionListener(new SelectionListener() {
				          public void widgetSelected(SelectionEvent e) {
				            dialogMsg.close();
				          }

				          public void widgetDefaultSelected(SelectionEvent e) {
				          }
				        });

				        dialogMsg.pack();
				        dialogMsg.open();
				
//				MessageDialog.openError(shell, "Error", message);
			}
		});
	}
	private static boolean checkStr(String str, String pattan){
		if("".equals(str.trim() ))
			showMessage("Check your \""+pattan+"\"");
		
		return "".equals(str.trim() );
	}
	
	private static void barUpdate(final ProgressBar bar,
			final Button clsBtn, final int[] i) {
			Display.getDefault().syncExec(new Runnable() {
			public void run() {
				
				if (bar.isDisposed())
					return;
				// bar update
				bar.setSelection(i[0]+1);
				if (bar.getMaximum() <=( i[0]+1)) {
					clsBtn.setEnabled(true);
				}
			}
		});
	}
}
