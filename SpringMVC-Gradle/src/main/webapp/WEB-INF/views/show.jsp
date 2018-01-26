<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8" />
    <title>My Bookmark Explorer</title>
    <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/gijgo/1.7.0/combined/js/gijgo.min.js" type="text/javascript"></script>
     <!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
   <!-- Required Stylesheets -->
<link href="bootstrap.css" rel="stylesheet">

<!-- Required Javascript -->
<script src="https://raw.githubusercontent.com/jonmiles/bootstrap-treeview/master/public/js/bootstrap-treeview.js"></script>
<script src="https://rawgit.com/jonmiles/bootstrap-treeview/master/public/js/bootstrap-treeview.js"></script>
    <link href="https://raw.githubusercontent.com/jonmiles/bootstrap-treeview/master/public/css/bootstrap-treeview.css" rel="stylesheet" type="text/css" />
    <link href="https://rawgit.com/jonmiles/bootstrap-treeview/master/public/css/bootstrap-treeview.css" rel="stylesheet" type="text/css" />
<script src="https://github.com/jonmiles/bootstrap-treeview"></script>

    <link href="https://cdnjs.cloudflare.com/ajax/libs/gijgo/1.7.0/combined/css/gijgo.min.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
   <c:set var="json" value="${json}"/>
    <%
    String json = ""; 
    json = (String)pageContext.getAttribute("json");  
    json = json.replaceAll("name", "text");
    json = json.replaceAll("subfolders", "nodes");
  %>  
</head>
<body>
<div class = "container">
<div class="row">
        <hr>
        <h2><center>My Bookmark Explorer</center></h2>
        <!-- create 3 columns using bootstrap -->
        <div class="col-sm-4">
          <h2>Search</h2>
            <div class="form-group">
              <label for="input-search" class="sr-only">Search Tree:</label>
              <input type="input" class="form-control" id="input-search" placeholder="Search Folders..." value="">
            </div>
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-ignore-case" value="false">
                Ignore Case
              </label>
            </div>
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-exact-match" value="false">
                Exact Match
              </label>
            </div>
            <button type="button" class="btn btn-success" id="btn-search">Search</button>
            <button type="button" class="btn btn-default" id="btn-clear-search">Clear</button>
        </div>
        <div class="col-sm-4">
          <h2>Tree</h2>
                  <!-- tree goes here -->
          <div id="treeview-searchable" class="treeview"></div>
          
        </div>
        <div class="col-sm-4">
          <h2>Results</h2>
          <!-- results go here -->
          <div id="search-output"></div>
        </div>
      </div>


    <script type="text/javascript">
    var $myTree = $('#treeview-searchable').treeview({
    	<!-- my json data from structure is passed in here -->
    		data: <%=json%>,
    	});

    var search = function(e) {
    	<!-- search query -->
    	  var pattern = $('#input-search').val();
    	  <!-- apply ignore case or exact match if checked -->
    	  var options = {
    	    ignoreCase: $('#chk-ignore-case').is(':checked'),
    	    exactMatch: $('#chk-exact-match').is(':checked'),
    	  };
    	  <!-- perform the search -->
    	  var searchResults = $myTree.treeview('search', [ pattern, options ]);

    	  <!-- create output -->
    	  var output = '<p>' + searchResults.length + ' matches found</p>';
    	  $.each(searchResults, function (index, result) {
    	    output += '<p>- ' + result.text + '</p>';
    	  });
    	  $('#search-output').html(output);
    	}
    <!-- search on click search button -->
    	$('#btn-search').on('click', search);
    	<!-- search on keyup -->
    	$('#input-search').on('keyup', search);
    	<!-- clear button clears any previous search -->
    	$('#btn-clear-search').on('click', function (e) {
    	  $myTree.treeview('clearSearch');
    	  <!-- reset the search box and output -->
    	  $('#input-search').val('');
    	  $('#search-output').html('');
    	});

    
    

    </script>
        <h5>Add New Folder</h5>
<form action="../view/create" method="post">
    <table class="table table-bordered">
        <tbody>
            <!--  <tr><th>id</th><td><input type="text" name="id" required="required"></td></tr>-->
            <tr><th>Folder Name</th><td><input type="text" name="folder" required="required"></td></tr>
            <tr><th>Parent Path</th><td><input type="text" name="parent"></td></tr>
           <tr><td colspan="2"><input type="submit" value="Add" class="btn btn-success" ></tr>
        </tbody>
    </table>
 
    
</form>

<h5>Delete Folder/Item</h5>
<form action="../view/delete" method="post">
    <table class="table table-bordered">
        <tbody>
            <!--  <tr><th>id</th><td><input type="text" name="id" required="required"></td></tr>-->
            <tr><th>Folder/Item Name</th><td><input type="text" name="folder" required="required"></td></tr>
           <tr><td colspan="2"> <input type="submit" value="Delete" class="btn btn-danger" ></tr>
        </tbody>
    </table>
    
</form>

<h5>Edit Folder/Item</h5>
<form action="../view/edit" method="post">
    <table class="table table-bordered">
        <tbody>
            <!--  <tr><th>id</th><td><input type="text" name="id" required="required"></td></tr>-->
            <tr><th>New Name</th><td><input type="text" name="folder" required="required"></td></tr>
            <tr><th>Item/Folder Full Path</th><td><input type="text" name="full_path" required="required"></td></tr>
           <tr><td colspan="2"><input type="submit" value="Edit" class="btn btn-warning" ></tr>
        </tbody>
    </table>
    
</form>
<h5>Add Link</h5>
<form action="../view/createlink" method="post">
    <table class="table table-bordered">
        <tbody>
            <!--  <tr><th>id</th><td><input type="text" name="id" required="required"></td></tr>-->

            <tr><th>Link Name</th><td><input type="text" name="folder" required="required"></td></tr>
            <tr><th>URI</th><td><input type="text" name="uri" required="required"></td></tr>
            <tr><th>Title</th><td><input type="text" name="title" required="required"></td></tr>
            <tr><th>Parent Path</th><td><input type="text" name="parent" required="required"></td></tr>
           <tr><td colspan="2"><input type="submit" value="Add" class="btn btn-success" ></tr>
        </tbody>
    </table>
    
</form>
<h5>Add Location</h5>
<form action="../view/createlocation" method="post">
    <table class="table table-bordered">
        <tbody>
            <tr><th>Location Name</th><td><input type="text" name="folder" required="required"></td></tr>
            <tr><th>Latitude</th><td><input type="text" name="latitude" required="required"></td></tr>
            <tr><th>Longitude</th><td><input type="text" name="longitude" required="required"></td></tr>
            <tr><th>Parent Path</th><td><input type="text" name="parent" required="required"></td></tr>
           <tr><td colspan="2"><input type="submit" value="Add" class="btn btn-success" ></tr>
        </tbody>
    </table>
    
</form>
<h5>Add Textfile</h5>
<form action="../view/createtextfile" method="post">
    <table class="table table-bordered">
        <tbody>
            <tr><th>Textfile Name</th><td><input type="text" name="folder" required="required"></td></tr>
            <tr><th>Text</th><td><input type="text" name="text" required="required"></td></tr>
            <tr><th>Parent Path</th><td><input type="text" name="parent" required="required"></td></tr>
           <tr><td colspan="2"><input type="submit" value="Add" class="btn btn-success" ></tr>
        </tbody>
    </table>
    
</form>
<h5>Lock Folder</h5>
<form action="../view/lockfolder" method="post">
    <table class="table table-bordered">
        <tbody>
            <tr><th>Folder Full Path</th><td><input type="text" name="full_path" required="required"></td></tr>
           <tr><td colspan="2"><input type="submit" value="Lock" class="btn btn" ></tr>
        </tbody>
    </table>
    
</form>

</form>
<h5>Unlock Folder</h5>
<form action="../view/unlockfolder" method="post">
    <table class="table table-bordered">
        <tbody>
            <tr><th>Folder Full Path</th><td><input type="text" name="full_path" required="required"></td></tr>
           <tr><td colspan="2"><input type="submit" value="Unlock" class="btn btn" ></tr>
        </tbody>
    </table>
    
</form>

<form action ="../view/dropbox" method ="post">
<input type="submit" value="Sync to Dropbox" class="btn btn" >
</form>
<br />
<form action ="../view/treemap" method ="post">
<input type="submit" value="GoogleTreemap" class="btn btn" >
</form>
</div>
</body>
</html>