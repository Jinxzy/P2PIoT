<!DOCTYPE html>
<html lang="en">
<head>
    <#include "include/head.ftl">
</head>
<body>
    <#include "include/nav.ftl">

    <div class="container-fluid">
        <div class="row">

        <#include "include/sub-menu.ftl">


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
                            <h4>Photon #${photon.data.ID}</h4>
                            <span class="text-muted">Light level: ${photon.data.description.result}</span>
                        </div>

                    </#if>

                </div>
                <#if photon??>
                    <h2 class="sub-header">Photon data</h2>
                    <#include "include/chart.ftl">
                </#if>
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
    <script>
        $("#kill-node").bind('click', function(){
            url = $(this).data('kill');
            redirect = $(this).data('follow');
            $.post(url, function(){
                document.location.href = redirect;
            });
            return false;
        });
    </script>
</body>
</html>