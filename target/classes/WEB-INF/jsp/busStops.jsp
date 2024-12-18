<c:if test="${not empty busStops}">
    <table class="table table-bordered">
        <thead>
            <tr>
                <th>Main Bus Stop Name</th>
                <th>Sub Locations (Bus Points)</th>
            </tr>
        </thead>
        <tbody>
            <!-- Loop through busStops -->
            <c:forEach var="busStop" items="${busStops}">
                <tr>
                    <td>${busStop.name}</td>
                    <td>
                        <!-- Loop through busPoints for each bus stop -->
                        <ul>
                            <c:forEach var="subStop" items="${busStop.busPoints}">
                                <li>
                                    Name: ${subStop.name}, Latitude: ${subStop.latitude}, Longitude: ${subStop.longitude}
                                </li>
                            </c:forEach>
                        </ul>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

<!-- Handle case when no bus stops are available -->
<c:if test="${empty busStops}">
    <div class="alert alert-warning">No bus stops found.</div>
</c:if>
