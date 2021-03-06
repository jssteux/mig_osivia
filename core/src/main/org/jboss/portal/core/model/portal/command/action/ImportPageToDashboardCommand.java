/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.core.model.portal.command.action;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.CustomizationManager;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.identity.User;

import java.util.Iterator;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8786 $
 */
public class ImportPageToDashboardCommand extends PageCommand
{

   /** . */
   private static final CommandInfo info = new ActionCommandInfo(false);

   /** . */
   private Portal dashboardPortal;

   public ImportPageToDashboardCommand(PortalObjectId pageId)
   {
      super(pageId);
   }

   public CommandInfo getInfo()
   {
      return info;
   }

   protected Page initPage()
   {
      return (Page)getTarget();
   }

   public void acquireResources() throws NoSuchResourceException
   {
      super.acquireResources();

      //
      CustomizationManager manager = getControllerContext().getController().getCustomizationManager();

      //
      User user = getControllerContext().getUser();

      //
      dashboardPortal = manager.getDashboard(user);
   }

   public ControllerResponse execute() throws ControllerException
   {
      String name = target.getName();

      //
      PortalObject dashboardPage = dashboardPortal.getChild(name);

      //
      if (dashboardPage == null)
      {
         try
         {
            // Copy page
            dashboardPage = target.copy(dashboardPortal, name, false);

            // Copy children windows only
            for (Iterator i = target.getChildren().iterator(); i.hasNext();)
            {
               PortalObject child = (PortalObject)i.next();
               if (child.getType() == PortalObject.TYPE_WINDOW)
               {
                  child.copy(dashboardPage, child.getName(), false);
               }
            }
         }
         catch (DuplicatePortalObjectException e)
         {
            log.error("Unexpected exception during the copy of a page to a dashboard page", e);

            //
            throw new ControllerException(e);
         }
      }

      return new UpdatePageResponse(dashboardPage.getId());
   }
}
