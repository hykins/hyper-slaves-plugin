/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 HyperHQ Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package sh.hyper.hyperslaves.spec;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Job;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class ContainerSetDefinition extends JobProperty {

    private final ContainerDefinition buildHostImage;


    @DataBoundConstructor
    public ContainerSetDefinition(ContainerDefinition buildHostImage) {
        this.buildHostImage = buildHostImage;
    }

    /**
     * When deserializing the config.xml file, XStream will instantiate a JobBuildsContainersDefinition
     * without going through the constructor; this means that any checks or default values that might
     * have been written in said constructor will be bypassed.
     * <p>
     * Fortunately, XStream calls the <code>readResolve</code> before the deserialized object
     * is returned to its parent. We simply recreate a JobBuildsContainersDefinition using the
     * deserialized values to replace the original one.
     *
     * @return a replacement JobBuildsContainersDefinition that went through the constructor
     */
    private Object readResolve() {
        //return new ContainerSetDefinition(buildHostImage, sideContainers);
        return new ContainerSetDefinition(buildHostImage);
    }

    public ContainerDefinition getBuildHostImage() {
        return buildHostImage;
        // return StringUtils.isBlank(buildHostImage) ? DockerSlaves.get().getDefaultBuildContainerImageName() : buildHostImage;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Containers to host the build";
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            if (formData.isNullObject()) return null;
            JSONObject containersDefinition = formData.getJSONObject("containersDefinition");
            if (containersDefinition.isNullObject()) return null;
            return req.bindJSON(ContainerSetDefinition.class, containersDefinition);
        }
    }

}
