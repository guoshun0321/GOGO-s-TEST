package jetsennet.jsmp.nav.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.ddl.MySqlDdl;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.FieldTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDBFileParse
{

	private static final Logger logger = LoggerFactory.getLogger(BDBFileParse.class);

	public void parseFolder(String path) throws Exception
	{

		File file = new File(path);
		logger.debug("path : " + file.getAbsolutePath());
		if (file.isDirectory())
		{
			// 删除文件
			File[] fileLst = file.listFiles();
			for (File f : fileLst)
			{
				if (f.isFile())
				{
					if (f.getName().endsWith(".java"))
					{
						f.delete();
					}
				}
			}

			// 生成TableInfo
			List<TableInfo> tblInfoLst = new ArrayList<TableInfo>();
			for (File f : fileLst)
			{
				if (f.isFile() && f.getName().endsWith(".ax"))
				{
					logger.debug("解析文件：" + f.getName());
					TableInfo ti = this.genTableInfo(f.getAbsolutePath());
					tblInfoLst.add(ti);
				}
			}

			// 更新数据库
			logger.debug("数据库初始化开始！");
			MySqlDdl ddl = new MySqlDdl(new ConfigurationBuilderProp("/dbconfig.mysql.media.properties").genConfiguration());
			for (TableInfo tbl : tblInfoLst)
			{
				logger.debug("初始化表：" + tbl.getTableName());
				ddl.delete(tbl.getTableName());
				ddl.create(tbl);
			}

			// 生成文件
			//			for (TableInfo tbl : tblInfoLst)
			//			{
			//				String fileName = tbl.getTableName();
			//				fileName = this.dbName2EntityName(fileName);
			//				OutputStream out = new BufferedOutputStream(new FileOutputStream(path + "/" + fileName + "Entity.java"));
			//				String javaFile = this.genJavaFile(tbl);
			//				out.write(javaFile.getBytes());
			//				out.flush();
			//				out.close();
			//				out = null;
			//			}
		}
		System.out.println(path);
		System.out.println("数据库初始化完成！");

	}

	public TableInfo genTableInfo(String path) throws Exception
	{
		SAXBuilder builder = new SAXBuilder();
		InputStream file = new BufferedInputStream(new FileInputStream(path));
		Document document = builder.build(file);//获得文档对象
		Element root = document.getRootElement();//获得根节点
		Element tableEle = root.getChild("TABLE");
		String dbName = tableEle.getAttributeValue("DISPLAYNAME");

		TableInfo retval = new TableInfo(null, dbName);

		List<Element> list = tableEle.getChild("ROWS").getChildren("ROW");
		List<FieldInfo> fields = new ArrayList<FieldInfo>(list.size());
		for (Element e : list)
		{
			String aName = e.getAttributeValue("NAME");
			String type = e.getAttributeValue("TYPENAME");
			if (type.equalsIgnoreCase("nvarchar") || type.equalsIgnoreCase("NVARCHAR2") || type.equalsIgnoreCase("VARCHAR2"))
			{
				type = "String";
			}
			else if (type.equalsIgnoreCase("datetime"))
			{
				type = "DATETIME";
			}
			else if (type.equalsIgnoreCase("int"))
			{
				type = "int";
			}
			else if (type.equalsIgnoreCase("long") || type.equalsIgnoreCase("BIGINT"))
			{
				type = "long";
			}
			else
			{
				throw new UncheckedNavException(String.format("列：%s，未知类型：%s", aName, type));
			}
			int length = Integer.valueOf(e.getAttributeValue("LENGTH"));
			int index = Integer.valueOf(e.getAttributeValue("INDEX"));
			String desc = e.getAttributeValue("REMARK");

			FieldInfo f = new FieldInfo(aName, type).length(length);
			f.setOrder(index);
			f.setDesc(desc);
			fields.add(f);
		}

		this.sortFields(fields);
		for (FieldInfo field : fields)
		{
			retval.field(field);
		}

		list = tableEle.getChild("TABLEINDEXS").getChildren("INDEX");
		for (Element e : list)
		{
			String aName = e.getAttributeValue("COLUMNNAME");
			retval.key(aName);
		}

		file.close();
		file = null;
		return retval;
	}

	public void sortFields(List<FieldInfo> fields)
	{
		Collections.sort(fields, new Comparator<FieldInfo>()
		{
			@Override
			public int compare(FieldInfo f1, FieldInfo f2)
			{
				if (f1.getOrder() >= f2.getOrder())
				{
					return 1;
				}
				else if (f1.getOrder() == f2.getOrder())
				{
					return 0;
				}
				else
				{
					return -1;
				}
			}
		});
	}

	public String genJavaFile(TableInfo info) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append("package jetsennet.jsmp.nav.entity;\n\n");
		sb.append("import java.io.Serializable;\n");
		sb.append("import java.util.Date;\n\n");
		sb.append("import jetsennet.orm.annotation.Column;\n");
		sb.append("import jetsennet.orm.annotation.Table;\n");
		sb.append("import jetsennet.orm.annotation.Id;\n");
		sb.append("/**\n");
		//        sb.append(" * 栏目实体\n");
		sb.append(" * \n");
		sb.append(" */\n");

		String dbName = info.getTableName();
		String name = this.dbName2EntityName(dbName);

		sb.append("@Table(\"").append(dbName).append("\")\n");
		sb.append("public class ").append(name).append("Entity implements Serializable").append("\n{\n");

		List<String> names = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		List<FieldInfo> fields = info.getFieldInfos();
		for (FieldInfo e : fields)
		{
			String attrName = e.getName();
			String aName = attrName(attrName);

			FieldTypeEnum typeE = e.getType();
			String type = typeE.name();

			String desc = e.getDesc();
			sb.append("\t/**\n");
			sb.append("\t * ").append(desc).append("\n");
			sb.append("\t */\n");

			if (e.isKey())
			{
				sb.append("\t@Id\n");
			}

			if (typeE == FieldTypeEnum.STRING)
			{
				type = "String";
			}
			else if (typeE == FieldTypeEnum.DATETIME)
			{
				type = "Date";
			}
			else if (typeE == FieldTypeEnum.INT)
			{
				type = "int";
			}
			else if (typeE == FieldTypeEnum.LONG)
			{
				type = "long";
			}
			sb.append("\t@Column(\"").append(attrName).append("\")\n");
			sb.append("\tprivate ").append(type).append(" ").append(aName).append(";\n");
			names.add(aName);
			types.add(type);
		}
		sb.append("\n\tprivate static final long serialVersionUID = 1L;\n\n");
		for (int i = 0; i < names.size(); i++)
		{
			// get
			sb.append("\tpublic ").append(types.get(i)).append(" get").append(firstUp(names.get(i))).append("()\n").append("\t{\n");
			sb.append("\t\treturn ").append(names.get(i)).append(";\n");
			sb.append("\t}\n\n");

			// set 
			sb.append("\tpublic void ")
				.append("set")
				.append(firstUp(names.get(i)))
				.append("(")
				.append(types.get(i))
				.append(" ")
				.append(names.get(i))
				.append(")\n")
				.append("\t{\n");
			sb.append("\t\tthis.").append(names.get(i)).append(" = ").append(names.get(i)).append(";\n");
			sb.append("\t}\n");
			sb.append("\n");
		}
		sb.append("}\n");
		System.out.println(sb.toString());
		return sb.toString();
	}

	private String firstUp(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	private String dbName2EntityName(String name)
	{
		name = name.toLowerCase();
		int pos = name.indexOf('_');
		name = name.substring(pos + 1);
		pos = name.indexOf('_');
		while (pos > 0 && pos < name.length() - 1)
		{
			name = name.substring(0, pos) + this.firstUp(name.substring(pos + 1));
			pos = name.indexOf('_');
		}
		name = this.firstUp(name);
		pos = name.indexOf('2');
		while (pos > 0 && pos < name.length() - 1)
		{
			name = name.substring(0, pos) + "$SEC$" + this.firstUp(name.substring(pos + 1));
			pos = name.indexOf('2');
		}
		name = name.replace("$SEC$", "2");
		if (name.equals("Fileitem"))
		{
			name = "FileItem";
		}
		else if (name.equals("Pgmbase"))
		{
			name = "pgmBase";
		}
		else if (name.equals("Physicalchannel"))
		{
			name = "PhysicalChannel";
		}
		else if (name.equals("Playbillitem"))
		{
			name = "PlaybillItem";
		}
		else if (name.equals("Relateblack"))
		{
			name = "RelateBlack";
		}
		else if (name.equals("Relatecolumn"))
		{
			name = "RelateColumn";
		}
		return name;
	}

	private String attrName(String name)
	{
		int pos = name.indexOf("_");
		name = name.toLowerCase();
		if (pos >= 0)
		{
			while (pos > 0 && pos < name.length() - 1)
			{
				name = name.substring(0, pos) + this.firstUp(name.substring(pos + 1));
				pos = name.indexOf("_");
			}
		}
		return name;
	}

	public static void main(String[] args) throws Exception
	{
		BDBFileParse reader = new BDBFileParse();
		reader.parseFolder("src/main/resources/dbscript/scheme");
	}

}
