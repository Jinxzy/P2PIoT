<!DOCTYPE html>
<html lang="en">
<head>
    <#include "include/head.ftl">
    <link href="http://www.bootstrap-switch.org/dist/css/bootstrap3/bootstrap-switch.css" rel="stylesheet">
</head>
<body>
    <#include "include/nav.ftl">

    <div class="container-fluid">
        <div class="row">

            <#include "include/sub-menu.ftl">

            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <h1 class="page-header">PHOTON</h1>
                <form role="form">
                    <div class="form-group">
                        <h3 class="">Light</h3>
                        <input type="checkbox" name="light">
                    </div>
                </form>


            </div>
        </div>
    </div>
    <script src="http://www.bootstrap-switch.org/dist/js/bootstrap-switch.js"></script>
    <script>
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
    </script>
</body>
</html>