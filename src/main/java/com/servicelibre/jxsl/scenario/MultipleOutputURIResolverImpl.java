/**
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

package com.servicelibre.jxsl.scenario;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.lib.OutputURIResolver;

public class MultipleOutputURIResolverImpl implements MultipleOutputURIResolver
{

    static Logger logger = LoggerFactory.getLogger(MultipleOutputURIResolverImpl.class);

    private Map<String, StringWriter> outputs = new HashMap<String, StringWriter>();

    public void close(Result output) throws TransformerException
    {

        if (output instanceof StreamResult)
        {
            try
            {
                ((StreamResult) output).getWriter().close();
            }
            catch (IOException e)
            {
                throw new TransformerException(e);
            }
        }
        else
        {
            logger.error("Unknown output type {}", output.getClass());
        }
    }

    public Result resolve(String href, String base) throws TransformerException
    {
        StringWriter writer = new StringWriter();
        outputs.put(href, writer);
        return new StreamResult(writer);
    }

    public StringWriter getWriter(String name)
    {
        if (outputs.containsKey(name))
        {
            StringWriter writer = outputs.get(name);
            return writer;
        }
        return null;
    }

    public void clearResults()
    {
        outputs.clear();
    }

    public Map<String, StringWriter> getOutputs()
    {
       return outputs;
    }

	@Override
	public OutputURIResolver newInstance() {
		return this;
	}

}
