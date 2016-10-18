<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CSE6331 Visualizing clusters</title>
</head>
<body bgcolor="#E6E6FA">
<form id = "form1" method ="POST" action="TitanicServlet" enctype = "multipart/form-data">
<header align = "center">
    <h1>Visualizing clusters</h1>
    <p>Browse the input file and click on Visualize Button.</p>
  </header>
<table border = "0" cellspacing="10" align = "center">
<tr>
<td>
<b>Input File</b>
<input type="file" id="fileupload" name="fileupload" accept = ".csv" />
</td>
</tr>

<tr>
<td>
<b>Column1</b>
<select id = 'col1' name='column1'>
 <option>age</option>
 <option>survived</option>
</select>
</td>
</tr>

<tr>
<td>
<b>Column2</b>
<select id = 'col2' name='column2'>
 <option>age</option>
 <option>survived</option>
</select>
</td>
</tr>

<tr>
<td>
<b>Input the desried number of clusters &nbsp&nbsp&nbsp</b>
<input type = "text" name = "clusters" id = "clusters" />
</td>
</tr>
<tr>
<td align="center">
<input type = "submit" value = "Visualize Clusters" />
</td>
</tr>
</table>
</form>
</body>
</html>