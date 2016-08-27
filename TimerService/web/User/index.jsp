<%@page import="classes.App"%>
<%@page contentType="text/html" pageEncoding="UTF-8" import="javax.servlet.http.HttpSession, java.sql.*, java.io.*, java.util.Date"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dashboard</title>
        <link href="../bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    </head>
    <body>
        <div class="container" style="margin-top: 25px;">
            <div class="row">
                <div class="col-lg-1"></div>
                <div class="col-lg-10">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="panel-title">
                                <h2 style="margin: 0px;">Dashboard</h2>
                            </div>
                        </div>
                        <div class="panel-body">
                            <h3>Your active timers -</h3><br>
                            <table class="table table-bordered table-hover">
                                <tr>
                                    <th>S. No</th>
                                    <th>Expiry Date</th>
                                </tr>
                                <%
                                    try {
                                        Class.forName(App.DRIVER_CLASS);

                                        try (Connection con = DriverManager.getConnection(App.CONNECTION_STRING)) {
                                            String query = "select expiry from " + App.TIMERS_TABLE + " where email = ? and expiry > dateadd(minute, 330, sysutcdatetime()) order by expiry";
                                            PreparedStatement pst = con.prepareStatement(query);

                                            pst.setString(1, request.getSession().getAttribute("email").toString());

                                            ResultSet rs = pst.executeQuery();

                                            int i = 1;
                                            while (rs.next()) {
                                %>
                                <tr>
                                    <td><%= i%></td>
                                    <td><%= rs.getString(1)%></td>
                                </tr>
                                <%

                                                ++i;
                                            }

                                            pst.close();
                                            rs.close();
                                        }
                                    } catch (Exception ex) {
                                        try {
                                            BufferedWriter bw = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/") + "/log.txt"));

                                            bw.write("Exception in /Users/index.jsp - " + ex.toString() + "\n");

                                            bw.close();
                                        } catch (Exception e) {
                                            System.out.println("Could not write to log");
                                        }

                                        System.out.println("Exception in index.java - " + ex.getLocalizedMessage());
                                        ex.printStackTrace();
                                    }
                                %>
                            </table>
                            <hr>
                            <p><b>Note</b> - An alert box will pop-up when your timer has expired!</p>
                        </div>
                        <div class="panel-footer clearfix">
                            <a href="addTimer.html" class="btn btn-info">Add a timer</a>
                            <a href="/TimerService/Logout" class="btn btn-danger pull-right">Logout</a>
                        </div>
                    </div>
                </div>
                <div class="col-lg-1"></div>
            </div>
        </div>
        <script src="../jquery/jquery-3.1.0.min.js"></script>
        <script src="../bootstrap/js/bootstrap.min.js"></script>
        <script>
            $(function () {
                checkTimer();
            });

            function checkTimer() {
                $.post("/TimerService/CheckTimer", null, checkTimerCallback);
            }

            function checkTimerCallback(data) {
                var sec = parseInt(data);
                console.log(sec);

                if (sec >= 1) {
                    console.log("sleeping...");

                    setTimeout(function () {
                        alert("Your timer has expired! Time to make history!");
                        checkTimer();
                    }, sec * 1000);

                    console.log("wake!");
                } else if (sec === -1) {
                    return;
                }
            }
        </script>
    </body>
</html>
