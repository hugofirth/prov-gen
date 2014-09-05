package uk.ac.ncl.prov.start

/**
 * prov-gen
 *
 * Copyright (c) 2014 Hugo Firth
 * Email: <me@hugofirth.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/**
 * Project entry object, defined in pom.xml build file.
 *
 * @author hugofirth
 */
object JettyLauncher {

  def main(args: Array[String]): Unit = {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 80

    val server = new Server(port)
    val context = new WebAppContext()

    //Find resource base relative to classpath
    val resources = this.getClass.getResource("/uk/ac/ncl/prov/webapp").toString

    context setContextPath "/"
    context.setResourceBase(resources+"/app")
    context.setInitParameter(ScalatraListener.LifeCycleKey, "uk.ac.ncl.prov.start.Bootstrap")
    context.addEventListener(new ScalatraListener)
//    context.addServlet(classOf[uk.ac.ncl.prov.controller.ConstraintController], "/api/v1/*")

    server.setHandler(context)

    server.start()
    server.join()
  }

}
