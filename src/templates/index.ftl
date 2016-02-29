<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>P2P / IoT</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">

    <!-- Custom styles for this template -->
    <link href="http://getbootstrap.com/examples/dashboard/dashboard.css" rel="stylesheet">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
    <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="${node.index}">P2P / IoT - Charlie</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="${node.index}">#${node.ID}</a></li>
                </ul>
                <form class="navbar-form navbar-right">
                    <input type="text" class="form-control" placeholder="Search... NOT IMPLEMENTED">
                </form>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-3 col-md-2 sidebar">
                <ul class="nav nav-sidebar">
                    <li class="active"><a href="#">Node <span class="sr-only">(current)</span></a></li>
                    <li><a href="${predecessor.index}">Predecessor</a></li>
                    <li><a href="${successor.index}">Successor</a></li>
                    <#if photon??>
                        <li><a href="${photon.link}">Photon</a></li>
                    </#if>

                </ul>
            </div>

            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <h1 class="page-header">#${node.ID}</h1>

                <div class="row placeholders">
                    <div class="col-xs-6 col-sm-3 placeholder">
                        <h4>Predecessor #${predecessor.ID}</h4>
                        <span class="text-muted">Listening at: <a href="${predecessor.index}">${predecessor.link}</a></span>
                    </div>
                    <div class="col-xs-6 col-sm-3 placeholder">
                        <h4>Successor #${successor.ID}</h4>
                        <span class="text-muted">Listening at: <a href="${successor.index}">${successor.link}</a></span>
                    </div>
                    <#if photon??>
                        <div class="col-xs-6 col-sm-3 placeholder">
                            <h4>Photon #${photon.id}</h4>
                            <span class="text-muted">Light level: ${photon.data.result}</span>
                        </div>

                    </#if>

                </div>

                <h2 class="sub-header">Fingers Table</h2>
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>ID</th>
                            <th>Belongs to</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list fingers as finger>
                            <#if finger??>
                            <tr>
                                <td>${finger_index}</td>
                                <td>${node.generateFingerId(finger_index)}</td>
                                <td>#${finger.ID} listening at <a href="${finger.index}">${finger.link}</a></td>
                            </tr>
                            </#if>
                        </#list>
                         </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

</body>
</html>