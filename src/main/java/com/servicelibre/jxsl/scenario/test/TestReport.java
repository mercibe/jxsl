/*
 * Java XSL code library
 *
 * Copyright (C) 2010 Benoit Mercier <info@servicelibre.com> — All rights reserved.
 *
 * This file is part of jxsl.
 *
 * jxsl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * jxsl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jxsl.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.servicelibre.jxsl.scenario.test;

import java.net.URL;
import java.util.Date;

public class TestReport
{

    public boolean success;
    public Date executionDate;
    public long executionTime;
    
    public int testCount;
    public int testFailedCount;
    
    
    public URL reportUrl;
    public FailureReport failureReport;

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("TestReport\n----------\n");
        sb.append("Success: ").append(success).append("\n");
        sb.append("Date: ").append(executionDate).append("\n");
        sb.append("Duration: ").append(executionTime).append("\n");
        sb.append("Detailed report URL: ").append(reportUrl).append("\n");
        if(!success) {
            sb.append("Failure report: ").append(failureReport).append("\n");
        }
        return sb.toString();
    }

}
