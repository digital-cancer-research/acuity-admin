<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="/" id="adminMainPage">ACUITY Admin</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav pull-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle pull-right" data-toggle="dropdown">
                        <span class="glyphicon glyphicon-user"></span>
                        <sec:authentication property="name"/>
                    </a>

                </li>
            </ul>
        </div>
    </div>
</div>
