/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 *
 * @author Anuar Nurmakanov
 */
@Controller
@RequestMapping("/plugins")
public class PluginController {

    private PluginService pluginService;

    @Autowired
    public PluginController(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView getPlugins() {
        List<Plugin> plugins = pluginService.getPlugins();
        return new ModelAndView("pluginsList")
                .addObject("plugins", plugins);
    }

    @RequestMapping(value = "/configure/{pluginId}", method = RequestMethod.GET)
    public ModelAndView configurePlugin(@PathVariable long pluginId) throws NotFoundException {
        PluginConfiguration pluginConfiguration = pluginService.get(pluginId);
        return new ModelAndView("pluginConfiguration")
                .addObject("plugin", pluginConfiguration);
    }
}
