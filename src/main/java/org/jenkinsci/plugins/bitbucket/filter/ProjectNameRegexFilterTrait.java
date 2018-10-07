package org.jenkinsci.plugins.bitbucket.filter;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigatorRequest;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketRepository;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.scm.api.SCMNavigator;
import jenkins.scm.api.trait.SCMNavigatorContext;
import jenkins.scm.api.trait.SCMNavigatorRequest;
import jenkins.scm.api.trait.SCMNavigatorTrait;
import jenkins.scm.api.trait.SCMNavigatorTraitDescriptor;
import jenkins.scm.api.trait.SCMSourceFilter;
import jenkins.scm.impl.trait.Selection;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ProjectNameRegexFilterTrait extends SCMNavigatorTrait {

    /**
     * The regular expression.
     */
    @NonNull
    private final String regex;

    /**
     * The compiled {@link Pattern}.
     */
    @CheckForNull
    private transient Pattern pattern;

    /**
     * Stapler constructor.
     *
     * @param regex the regular expression.
     */
    @DataBoundConstructor
    public ProjectNameRegexFilterTrait(@NonNull String regex) {
        this.pattern = Pattern.compile(regex);
        this.regex = regex;
    }

    /**
     * Gets the regular expression.
     *
     * @return the regular expression.
     */
    @NonNull
    public String getRegex() {
        return regex;
    }

    /**
     * Gets the compiled {@link Pattern}.
     *
     * @return the compiled {@link Pattern}.
     */
    @NonNull
    private Pattern getPattern() {
        if (pattern == null) {
            // idempotent
            pattern = Pattern.compile(regex);
        }
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void decorateContext(SCMNavigatorContext<?, ?> context) {
        context.withFilter(new SCMSourceFilter() {
            @Override
            public boolean isExcluded(@NonNull SCMNavigatorRequest request, @NonNull String repositoryName) {
                BitbucketSCMNavigatorRequest bitbucketRequest = (BitbucketSCMNavigatorRequest) request;

                BitbucketRepository repository = bitbucketRequest.getBitbucketRepository(repositoryName);

                if (repository.getProject() == null) {
                    return false;
                }

                return !getPattern().matcher(repository.getProject().getName()).matches();
            }
        });
    }

    /**
     * Our descriptor.
     */
    @Symbol("projectNameRegexFilter")
    @Extension
    @Selection
    public static class DescriptorImpl extends SCMNavigatorTraitDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return Messages.ProjectNameRegexFilterTrait_displayName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Class<? extends SCMNavigator> getNavigatorClass() {
            return BitbucketSCMNavigator.class;
        }

        /**
         * Form validation for the regular expression.
         *
         * @param value the regular expression.
         * @return the validation results.
         */
        @Restricted(NoExternalUse.class) // stapler
        public FormValidation doCheckRegex(@QueryParameter String value) {
            try {
                Pattern.compile(value);
                return FormValidation.ok();
            } catch (PatternSyntaxException e) {
                return FormValidation.error(e.getMessage());
            }
        }

    }

}
