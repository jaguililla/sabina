<% root = content.rootpath ?: '' %>
<% title = content.title ?: 'Spark - A small web framework for Java' %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <title>${title}</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">
  <meta name="keywords" content="">
  <meta name="generator" content="JBake">

  <link
    href="${config.bootstrapcdn}/bootswatch/3.1.0/lumen/bootstrap.min.css"
    rel="stylesheet">
  <link href="${root}css/base.css" rel="stylesheet">
  <link href="${config.cloudflare}/prettify/r298/prettify.min.css" rel="stylesheet">

  <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
    <script src="${config.cloudflare}/html5shiv/3.7.2/html5shiv.min.js"></script>
  <![endif]-->

  <!--<link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
  <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
  <link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
  <link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">-->
  <link rel="shortcut icon" href="${root}favicon.ico">
</head>

<body onload="prettyPrint()">
  <div id="wrap">
    <p id="header">
      <img
        src="http://raw.githubusercontent.com/jamming/spark/gh-pages/sabina-logo.png"
        alt="Project Logo"/>
      <h1>
        A Sinatra inspired micro web framework for quickly creating web applications in Java
        with minimal effort
      </h1>
    </p>
