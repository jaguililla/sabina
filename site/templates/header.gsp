<% root = content.rootpath ?: '' %>
<% title = content.title ?: 'Sabina - A small web framework for Java' %>
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

  <!-- lumen, paper or yeti -->
  <link
    href="${config.bootstrapcdn}/bootswatch/3.2.0/yeti/bootstrap.min.css"
    rel="stylesheet">
  <link href="${root}css/base.css" rel="stylesheet">
  <link href="${config.cloudflare}/prettify/r298/prettify.min.css" rel="stylesheet">

  <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
    <script src="${config.cloudflare}/html5shiv/3.7.2/html5shiv.min.js"></script>
  <![endif]-->

  <link rel="shortcut icon" href="${root}favicon.ico">
</head>

<body onload="prettyPrint()">
  <div id="wrap">
