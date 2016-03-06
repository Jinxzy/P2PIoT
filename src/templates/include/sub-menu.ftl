<div class="col-sm-3 col-md-2 sidebar">
    <ul class="nav nav-sidebar">
        <li class="active"><a href="${node.index}"">Node <span class="sr-only">(current)</span></a></li>
        <li><a href="${predecessor.index}">Predecessor</a></li>
        <li><a href="${successor.index}">Successor</a></li>
    <#if photon??>
        <li><a href="${photon.link}">Photon</a></li>
    </#if>

    </ul>
</div>