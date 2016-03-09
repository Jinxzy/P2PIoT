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
                            <h4>Photon #${photon.id}</h4>
                            <div class="text-muted">Current light level: ${photon.data.result}</div>
                            <div class="text-muted">k: ${photon.replicas}</div>
                        </div>
                        <div class="col-xs-6 col-sm-3 placeholder">
                            <form role="form">
                                <div class="form-group">
                                    <h4 class="">Photon Led</h4>
                                    <input type="checkbox" name="light">
                                </div>
                            </form>
                        </div>

                    </#if>

                </div>
                <#if photon??>
                    <h2 class="sub-header">Photon data</h2>
                    <#include "include/chart.ftl">
                <#elseif photon_data??>
                    <h2 class="sub-header">Photon persistenced data with k: ${replicated}</h2>
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

    <script src="http://www.bootstrap-switch.org/dist/js/bootstrap-switch.js"></script>
    <script>

        // infering the current state of the led

        (function inferState(){
            $.get("/photon/light-data-last/1")
                    .done(function( data ) {
                        // guess the led is on
                        if(data['light'] > 100){
                            console.log('led status is on')
                            $("input[name='light']").attr('checked', true)
                        }else console.log('led status is off')

                        $("input[name='light']").bootstrapSwitch();


                        $('input[name="light"]').on('switchChange.bootstrapSwitch', function(event, state) {
                            // state true or false
                            var value = state?'on':'off';
                            var url = 'https://api.particle.io/v1/devices/2b0023000247343138333038/led?access_token=c2f1f7a26afd51a45e7ad921058164cbf08d1708'
                            $.post(url, {'args':value})
                                    .done(function( data ) {
                                        console.log(data)
                                    });
                        });


                    });
        })()





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