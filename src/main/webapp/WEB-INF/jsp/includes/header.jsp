<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <!--  local links for project  -->
    <link rel="stylesheet" href="../pub/css/BaseStyle.css">
    <link rel="stylesheet" href="../pub/css/gridStyle.css">
    <link rel="stylesheet" href="../pub/css/AnimalStyle.css">
    <!--  Link to font and other frameworks  -->
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;700&family=Roboto:wght@500&display=swap" rel="stylesheet">
    <!--  Remaining head information  -->
    <title>Title</title>
</head>
<body>
<div class="mainGridContainer">
    <header class="headerContent">
        <div id="siteLogo">
            <img src="../pub/images/farmLogo.png" alt="farmlogo">
            <span><strong>FMS</strong></span>
        </div>
        <div id="headerSearch">
            <input type="search" name="searchAnimal" id="searchAnimalId">
        </div>
        <div id="headerAccount">

        </div>


    </header>
    <main class="mainContentContainer">
        <nav id="navContainer">
            <ul class="navULContainer">
                <li class="navButton"><a href="../index">Index</a></li>
                <sec:authorize access="hasAnyAuthority('USER', 'ADMIN')">
                    <%-- TODO:Trying to make a drop-down => Gold Plate  --%>
                    <li class="navButton"><a href="../herd/addAnimal">Add Cow or Bull</a></li>
                    <li class="navButton"><a href="../herd/addCalf">Add Calf</a></li>
                </sec:authorize>
                <sec:authorize access="hasAnyAuthority('USER', 'ADMIN')">
                    <li class="navButton"><a href="../herd/list">List</a></li>
                </sec:authorize>
                <sec:authorize access="!isAuthenticated()">
                    <li class="navButton account"><a href="../login/login">Login</a></li>
                    <li class="navButton account"><a href="../user/register">Sign Up</a></li>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li class="navButton account"><a href="../login/logout">Logout</a></li>
                </sec:authorize>
            </ul>
        </nav>