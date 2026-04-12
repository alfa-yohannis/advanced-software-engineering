package yohannis.alfa.mediaflow.ide.contentassist.antlr.internal;

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.DFA;
import yohannis.alfa.mediaflow.services.MediaFlowGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalMediaFlowParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'true'", "'false'", "'VIDEO'", "'AUDIO'", "'SUBTITLE'", "'IMAGE'", "'IMAGE_SEQUENCE'", "'FFMPEG'", "'GSTREAMER'", "'CUSTOM'", "'IN'", "'OUT'", "'graph'", "'{'", "'}'", "'resource'", "'['", "'mediaType'", "'='", "']'", "'uri'", "'external'", "'scaler'", "'backend'", "'command'", "'replicas'", "'width'", "'height'", "'transcoder'", "'videoCodec'", "'audioCodec'", "'container'", "'bitrate'", "'port'", "'edge'", "'from'", "'to'"
    };
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int RULE_ID=4;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=6;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int RULE_STRING=5;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int RULE_WS=9;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;

    // delegates
    // delegators


        public InternalMediaFlowParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalMediaFlowParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalMediaFlowParser.tokenNames; }
    public String getGrammarFileName() { return "InternalMediaFlow.g"; }


    	private MediaFlowGrammarAccess grammarAccess;

    	public void setGrammarAccess(MediaFlowGrammarAccess grammarAccess) {
    		this.grammarAccess = grammarAccess;
    	}

    	@Override
    	protected Grammar getGrammar() {
    		return grammarAccess.getGrammar();
    	}

    	@Override
    	protected String getValueForTokenName(String tokenName) {
    		return tokenName;
    	}



    // $ANTLR start "entryRuleModel"
    // InternalMediaFlow.g:53:1: entryRuleModel : ruleModel EOF ;
    public final void entryRuleModel() throws RecognitionException {
        try {
            // InternalMediaFlow.g:54:1: ( ruleModel EOF )
            // InternalMediaFlow.g:55:1: ruleModel EOF
            {
             before(grammarAccess.getModelRule()); 
            pushFollow(FOLLOW_1);
            ruleModel();

            state._fsp--;

             after(grammarAccess.getModelRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // InternalMediaFlow.g:62:1: ruleModel : ( ruleGraph ) ;
    public final void ruleModel() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:66:2: ( ( ruleGraph ) )
            // InternalMediaFlow.g:67:2: ( ruleGraph )
            {
            // InternalMediaFlow.g:67:2: ( ruleGraph )
            // InternalMediaFlow.g:68:3: ruleGraph
            {
             before(grammarAccess.getModelAccess().getGraphParserRuleCall()); 
            pushFollow(FOLLOW_2);
            ruleGraph();

            state._fsp--;

             after(grammarAccess.getModelAccess().getGraphParserRuleCall()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleGraph"
    // InternalMediaFlow.g:78:1: entryRuleGraph : ruleGraph EOF ;
    public final void entryRuleGraph() throws RecognitionException {
        try {
            // InternalMediaFlow.g:79:1: ( ruleGraph EOF )
            // InternalMediaFlow.g:80:1: ruleGraph EOF
            {
             before(grammarAccess.getGraphRule()); 
            pushFollow(FOLLOW_1);
            ruleGraph();

            state._fsp--;

             after(grammarAccess.getGraphRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleGraph"


    // $ANTLR start "ruleGraph"
    // InternalMediaFlow.g:87:1: ruleGraph : ( ( rule__Graph__Group__0 ) ) ;
    public final void ruleGraph() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:91:2: ( ( ( rule__Graph__Group__0 ) ) )
            // InternalMediaFlow.g:92:2: ( ( rule__Graph__Group__0 ) )
            {
            // InternalMediaFlow.g:92:2: ( ( rule__Graph__Group__0 ) )
            // InternalMediaFlow.g:93:3: ( rule__Graph__Group__0 )
            {
             before(grammarAccess.getGraphAccess().getGroup()); 
            // InternalMediaFlow.g:94:3: ( rule__Graph__Group__0 )
            // InternalMediaFlow.g:94:4: rule__Graph__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Graph__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getGraphAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleGraph"


    // $ANTLR start "entryRuleNode"
    // InternalMediaFlow.g:103:1: entryRuleNode : ruleNode EOF ;
    public final void entryRuleNode() throws RecognitionException {
        try {
            // InternalMediaFlow.g:104:1: ( ruleNode EOF )
            // InternalMediaFlow.g:105:1: ruleNode EOF
            {
             before(grammarAccess.getNodeRule()); 
            pushFollow(FOLLOW_1);
            ruleNode();

            state._fsp--;

             after(grammarAccess.getNodeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleNode"


    // $ANTLR start "ruleNode"
    // InternalMediaFlow.g:112:1: ruleNode : ( ( rule__Node__Alternatives ) ) ;
    public final void ruleNode() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:116:2: ( ( ( rule__Node__Alternatives ) ) )
            // InternalMediaFlow.g:117:2: ( ( rule__Node__Alternatives ) )
            {
            // InternalMediaFlow.g:117:2: ( ( rule__Node__Alternatives ) )
            // InternalMediaFlow.g:118:3: ( rule__Node__Alternatives )
            {
             before(grammarAccess.getNodeAccess().getAlternatives()); 
            // InternalMediaFlow.g:119:3: ( rule__Node__Alternatives )
            // InternalMediaFlow.g:119:4: rule__Node__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Node__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getNodeAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleNode"


    // $ANTLR start "entryRuleTransformer"
    // InternalMediaFlow.g:128:1: entryRuleTransformer : ruleTransformer EOF ;
    public final void entryRuleTransformer() throws RecognitionException {
        try {
            // InternalMediaFlow.g:129:1: ( ruleTransformer EOF )
            // InternalMediaFlow.g:130:1: ruleTransformer EOF
            {
             before(grammarAccess.getTransformerRule()); 
            pushFollow(FOLLOW_1);
            ruleTransformer();

            state._fsp--;

             after(grammarAccess.getTransformerRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleTransformer"


    // $ANTLR start "ruleTransformer"
    // InternalMediaFlow.g:137:1: ruleTransformer : ( ( rule__Transformer__Alternatives ) ) ;
    public final void ruleTransformer() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:141:2: ( ( ( rule__Transformer__Alternatives ) ) )
            // InternalMediaFlow.g:142:2: ( ( rule__Transformer__Alternatives ) )
            {
            // InternalMediaFlow.g:142:2: ( ( rule__Transformer__Alternatives ) )
            // InternalMediaFlow.g:143:3: ( rule__Transformer__Alternatives )
            {
             before(grammarAccess.getTransformerAccess().getAlternatives()); 
            // InternalMediaFlow.g:144:3: ( rule__Transformer__Alternatives )
            // InternalMediaFlow.g:144:4: rule__Transformer__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Transformer__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getTransformerAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleTransformer"


    // $ANTLR start "entryRuleResource"
    // InternalMediaFlow.g:153:1: entryRuleResource : ruleResource EOF ;
    public final void entryRuleResource() throws RecognitionException {
        try {
            // InternalMediaFlow.g:154:1: ( ruleResource EOF )
            // InternalMediaFlow.g:155:1: ruleResource EOF
            {
             before(grammarAccess.getResourceRule()); 
            pushFollow(FOLLOW_1);
            ruleResource();

            state._fsp--;

             after(grammarAccess.getResourceRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleResource"


    // $ANTLR start "ruleResource"
    // InternalMediaFlow.g:162:1: ruleResource : ( ( rule__Resource__Group__0 ) ) ;
    public final void ruleResource() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:166:2: ( ( ( rule__Resource__Group__0 ) ) )
            // InternalMediaFlow.g:167:2: ( ( rule__Resource__Group__0 ) )
            {
            // InternalMediaFlow.g:167:2: ( ( rule__Resource__Group__0 ) )
            // InternalMediaFlow.g:168:3: ( rule__Resource__Group__0 )
            {
             before(grammarAccess.getResourceAccess().getGroup()); 
            // InternalMediaFlow.g:169:3: ( rule__Resource__Group__0 )
            // InternalMediaFlow.g:169:4: rule__Resource__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Resource__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getResourceAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleResource"


    // $ANTLR start "entryRuleScaler"
    // InternalMediaFlow.g:178:1: entryRuleScaler : ruleScaler EOF ;
    public final void entryRuleScaler() throws RecognitionException {
        try {
            // InternalMediaFlow.g:179:1: ( ruleScaler EOF )
            // InternalMediaFlow.g:180:1: ruleScaler EOF
            {
             before(grammarAccess.getScalerRule()); 
            pushFollow(FOLLOW_1);
            ruleScaler();

            state._fsp--;

             after(grammarAccess.getScalerRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleScaler"


    // $ANTLR start "ruleScaler"
    // InternalMediaFlow.g:187:1: ruleScaler : ( ( rule__Scaler__Group__0 ) ) ;
    public final void ruleScaler() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:191:2: ( ( ( rule__Scaler__Group__0 ) ) )
            // InternalMediaFlow.g:192:2: ( ( rule__Scaler__Group__0 ) )
            {
            // InternalMediaFlow.g:192:2: ( ( rule__Scaler__Group__0 ) )
            // InternalMediaFlow.g:193:3: ( rule__Scaler__Group__0 )
            {
             before(grammarAccess.getScalerAccess().getGroup()); 
            // InternalMediaFlow.g:194:3: ( rule__Scaler__Group__0 )
            // InternalMediaFlow.g:194:4: rule__Scaler__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getScalerAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleScaler"


    // $ANTLR start "entryRuleTranscoder"
    // InternalMediaFlow.g:203:1: entryRuleTranscoder : ruleTranscoder EOF ;
    public final void entryRuleTranscoder() throws RecognitionException {
        try {
            // InternalMediaFlow.g:204:1: ( ruleTranscoder EOF )
            // InternalMediaFlow.g:205:1: ruleTranscoder EOF
            {
             before(grammarAccess.getTranscoderRule()); 
            pushFollow(FOLLOW_1);
            ruleTranscoder();

            state._fsp--;

             after(grammarAccess.getTranscoderRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleTranscoder"


    // $ANTLR start "ruleTranscoder"
    // InternalMediaFlow.g:212:1: ruleTranscoder : ( ( rule__Transcoder__Group__0 ) ) ;
    public final void ruleTranscoder() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:216:2: ( ( ( rule__Transcoder__Group__0 ) ) )
            // InternalMediaFlow.g:217:2: ( ( rule__Transcoder__Group__0 ) )
            {
            // InternalMediaFlow.g:217:2: ( ( rule__Transcoder__Group__0 ) )
            // InternalMediaFlow.g:218:3: ( rule__Transcoder__Group__0 )
            {
             before(grammarAccess.getTranscoderAccess().getGroup()); 
            // InternalMediaFlow.g:219:3: ( rule__Transcoder__Group__0 )
            // InternalMediaFlow.g:219:4: rule__Transcoder__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleTranscoder"


    // $ANTLR start "entryRulePort"
    // InternalMediaFlow.g:228:1: entryRulePort : rulePort EOF ;
    public final void entryRulePort() throws RecognitionException {
        try {
            // InternalMediaFlow.g:229:1: ( rulePort EOF )
            // InternalMediaFlow.g:230:1: rulePort EOF
            {
             before(grammarAccess.getPortRule()); 
            pushFollow(FOLLOW_1);
            rulePort();

            state._fsp--;

             after(grammarAccess.getPortRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRulePort"


    // $ANTLR start "rulePort"
    // InternalMediaFlow.g:237:1: rulePort : ( ( rule__Port__Group__0 ) ) ;
    public final void rulePort() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:241:2: ( ( ( rule__Port__Group__0 ) ) )
            // InternalMediaFlow.g:242:2: ( ( rule__Port__Group__0 ) )
            {
            // InternalMediaFlow.g:242:2: ( ( rule__Port__Group__0 ) )
            // InternalMediaFlow.g:243:3: ( rule__Port__Group__0 )
            {
             before(grammarAccess.getPortAccess().getGroup()); 
            // InternalMediaFlow.g:244:3: ( rule__Port__Group__0 )
            // InternalMediaFlow.g:244:4: rule__Port__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Port__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getPortAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rulePort"


    // $ANTLR start "entryRuleEdge"
    // InternalMediaFlow.g:253:1: entryRuleEdge : ruleEdge EOF ;
    public final void entryRuleEdge() throws RecognitionException {
        try {
            // InternalMediaFlow.g:254:1: ( ruleEdge EOF )
            // InternalMediaFlow.g:255:1: ruleEdge EOF
            {
             before(grammarAccess.getEdgeRule()); 
            pushFollow(FOLLOW_1);
            ruleEdge();

            state._fsp--;

             after(grammarAccess.getEdgeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEdge"


    // $ANTLR start "ruleEdge"
    // InternalMediaFlow.g:262:1: ruleEdge : ( ( rule__Edge__Group__0 ) ) ;
    public final void ruleEdge() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:266:2: ( ( ( rule__Edge__Group__0 ) ) )
            // InternalMediaFlow.g:267:2: ( ( rule__Edge__Group__0 ) )
            {
            // InternalMediaFlow.g:267:2: ( ( rule__Edge__Group__0 ) )
            // InternalMediaFlow.g:268:3: ( rule__Edge__Group__0 )
            {
             before(grammarAccess.getEdgeAccess().getGroup()); 
            // InternalMediaFlow.g:269:3: ( rule__Edge__Group__0 )
            // InternalMediaFlow.g:269:4: rule__Edge__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Edge__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getEdgeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdge"


    // $ANTLR start "entryRuleEBoolean"
    // InternalMediaFlow.g:278:1: entryRuleEBoolean : ruleEBoolean EOF ;
    public final void entryRuleEBoolean() throws RecognitionException {
        try {
            // InternalMediaFlow.g:279:1: ( ruleEBoolean EOF )
            // InternalMediaFlow.g:280:1: ruleEBoolean EOF
            {
             before(grammarAccess.getEBooleanRule()); 
            pushFollow(FOLLOW_1);
            ruleEBoolean();

            state._fsp--;

             after(grammarAccess.getEBooleanRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEBoolean"


    // $ANTLR start "ruleEBoolean"
    // InternalMediaFlow.g:287:1: ruleEBoolean : ( ( rule__EBoolean__Alternatives ) ) ;
    public final void ruleEBoolean() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:291:2: ( ( ( rule__EBoolean__Alternatives ) ) )
            // InternalMediaFlow.g:292:2: ( ( rule__EBoolean__Alternatives ) )
            {
            // InternalMediaFlow.g:292:2: ( ( rule__EBoolean__Alternatives ) )
            // InternalMediaFlow.g:293:3: ( rule__EBoolean__Alternatives )
            {
             before(grammarAccess.getEBooleanAccess().getAlternatives()); 
            // InternalMediaFlow.g:294:3: ( rule__EBoolean__Alternatives )
            // InternalMediaFlow.g:294:4: rule__EBoolean__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__EBoolean__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getEBooleanAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEBoolean"


    // $ANTLR start "ruleMediaType"
    // InternalMediaFlow.g:303:1: ruleMediaType : ( ( rule__MediaType__Alternatives ) ) ;
    public final void ruleMediaType() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:307:1: ( ( ( rule__MediaType__Alternatives ) ) )
            // InternalMediaFlow.g:308:2: ( ( rule__MediaType__Alternatives ) )
            {
            // InternalMediaFlow.g:308:2: ( ( rule__MediaType__Alternatives ) )
            // InternalMediaFlow.g:309:3: ( rule__MediaType__Alternatives )
            {
             before(grammarAccess.getMediaTypeAccess().getAlternatives()); 
            // InternalMediaFlow.g:310:3: ( rule__MediaType__Alternatives )
            // InternalMediaFlow.g:310:4: rule__MediaType__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__MediaType__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getMediaTypeAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleMediaType"


    // $ANTLR start "ruleBackend"
    // InternalMediaFlow.g:319:1: ruleBackend : ( ( rule__Backend__Alternatives ) ) ;
    public final void ruleBackend() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:323:1: ( ( ( rule__Backend__Alternatives ) ) )
            // InternalMediaFlow.g:324:2: ( ( rule__Backend__Alternatives ) )
            {
            // InternalMediaFlow.g:324:2: ( ( rule__Backend__Alternatives ) )
            // InternalMediaFlow.g:325:3: ( rule__Backend__Alternatives )
            {
             before(grammarAccess.getBackendAccess().getAlternatives()); 
            // InternalMediaFlow.g:326:3: ( rule__Backend__Alternatives )
            // InternalMediaFlow.g:326:4: rule__Backend__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Backend__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getBackendAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleBackend"


    // $ANTLR start "ruleDirection"
    // InternalMediaFlow.g:335:1: ruleDirection : ( ( rule__Direction__Alternatives ) ) ;
    public final void ruleDirection() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:339:1: ( ( ( rule__Direction__Alternatives ) ) )
            // InternalMediaFlow.g:340:2: ( ( rule__Direction__Alternatives ) )
            {
            // InternalMediaFlow.g:340:2: ( ( rule__Direction__Alternatives ) )
            // InternalMediaFlow.g:341:3: ( rule__Direction__Alternatives )
            {
             before(grammarAccess.getDirectionAccess().getAlternatives()); 
            // InternalMediaFlow.g:342:3: ( rule__Direction__Alternatives )
            // InternalMediaFlow.g:342:4: rule__Direction__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Direction__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getDirectionAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleDirection"


    // $ANTLR start "rule__Node__Alternatives"
    // InternalMediaFlow.g:350:1: rule__Node__Alternatives : ( ( ruleResource ) | ( ruleTransformer ) );
    public final void rule__Node__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:354:1: ( ( ruleResource ) | ( ruleTransformer ) )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==26) ) {
                alt1=1;
            }
            else if ( (LA1_0==33||LA1_0==39) ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // InternalMediaFlow.g:355:2: ( ruleResource )
                    {
                    // InternalMediaFlow.g:355:2: ( ruleResource )
                    // InternalMediaFlow.g:356:3: ruleResource
                    {
                     before(grammarAccess.getNodeAccess().getResourceParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleResource();

                    state._fsp--;

                     after(grammarAccess.getNodeAccess().getResourceParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:361:2: ( ruleTransformer )
                    {
                    // InternalMediaFlow.g:361:2: ( ruleTransformer )
                    // InternalMediaFlow.g:362:3: ruleTransformer
                    {
                     before(grammarAccess.getNodeAccess().getTransformerParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleTransformer();

                    state._fsp--;

                     after(grammarAccess.getNodeAccess().getTransformerParserRuleCall_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Node__Alternatives"


    // $ANTLR start "rule__Transformer__Alternatives"
    // InternalMediaFlow.g:371:1: rule__Transformer__Alternatives : ( ( ruleScaler ) | ( ruleTranscoder ) );
    public final void rule__Transformer__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:375:1: ( ( ruleScaler ) | ( ruleTranscoder ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==33) ) {
                alt2=1;
            }
            else if ( (LA2_0==39) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // InternalMediaFlow.g:376:2: ( ruleScaler )
                    {
                    // InternalMediaFlow.g:376:2: ( ruleScaler )
                    // InternalMediaFlow.g:377:3: ruleScaler
                    {
                     before(grammarAccess.getTransformerAccess().getScalerParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleScaler();

                    state._fsp--;

                     after(grammarAccess.getTransformerAccess().getScalerParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:382:2: ( ruleTranscoder )
                    {
                    // InternalMediaFlow.g:382:2: ( ruleTranscoder )
                    // InternalMediaFlow.g:383:3: ruleTranscoder
                    {
                     before(grammarAccess.getTransformerAccess().getTranscoderParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleTranscoder();

                    state._fsp--;

                     after(grammarAccess.getTransformerAccess().getTranscoderParserRuleCall_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transformer__Alternatives"


    // $ANTLR start "rule__EBoolean__Alternatives"
    // InternalMediaFlow.g:392:1: rule__EBoolean__Alternatives : ( ( 'true' ) | ( 'false' ) );
    public final void rule__EBoolean__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:396:1: ( ( 'true' ) | ( 'false' ) )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==11) ) {
                alt3=1;
            }
            else if ( (LA3_0==12) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // InternalMediaFlow.g:397:2: ( 'true' )
                    {
                    // InternalMediaFlow.g:397:2: ( 'true' )
                    // InternalMediaFlow.g:398:3: 'true'
                    {
                     before(grammarAccess.getEBooleanAccess().getTrueKeyword_0()); 
                    match(input,11,FOLLOW_2); 
                     after(grammarAccess.getEBooleanAccess().getTrueKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:403:2: ( 'false' )
                    {
                    // InternalMediaFlow.g:403:2: ( 'false' )
                    // InternalMediaFlow.g:404:3: 'false'
                    {
                     before(grammarAccess.getEBooleanAccess().getFalseKeyword_1()); 
                    match(input,12,FOLLOW_2); 
                     after(grammarAccess.getEBooleanAccess().getFalseKeyword_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EBoolean__Alternatives"


    // $ANTLR start "rule__MediaType__Alternatives"
    // InternalMediaFlow.g:413:1: rule__MediaType__Alternatives : ( ( ( 'VIDEO' ) ) | ( ( 'AUDIO' ) ) | ( ( 'SUBTITLE' ) ) | ( ( 'IMAGE' ) ) | ( ( 'IMAGE_SEQUENCE' ) ) );
    public final void rule__MediaType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:417:1: ( ( ( 'VIDEO' ) ) | ( ( 'AUDIO' ) ) | ( ( 'SUBTITLE' ) ) | ( ( 'IMAGE' ) ) | ( ( 'IMAGE_SEQUENCE' ) ) )
            int alt4=5;
            switch ( input.LA(1) ) {
            case 13:
                {
                alt4=1;
                }
                break;
            case 14:
                {
                alt4=2;
                }
                break;
            case 15:
                {
                alt4=3;
                }
                break;
            case 16:
                {
                alt4=4;
                }
                break;
            case 17:
                {
                alt4=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // InternalMediaFlow.g:418:2: ( ( 'VIDEO' ) )
                    {
                    // InternalMediaFlow.g:418:2: ( ( 'VIDEO' ) )
                    // InternalMediaFlow.g:419:3: ( 'VIDEO' )
                    {
                     before(grammarAccess.getMediaTypeAccess().getVIDEOEnumLiteralDeclaration_0()); 
                    // InternalMediaFlow.g:420:3: ( 'VIDEO' )
                    // InternalMediaFlow.g:420:4: 'VIDEO'
                    {
                    match(input,13,FOLLOW_2); 

                    }

                     after(grammarAccess.getMediaTypeAccess().getVIDEOEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:424:2: ( ( 'AUDIO' ) )
                    {
                    // InternalMediaFlow.g:424:2: ( ( 'AUDIO' ) )
                    // InternalMediaFlow.g:425:3: ( 'AUDIO' )
                    {
                     before(grammarAccess.getMediaTypeAccess().getAUDIOEnumLiteralDeclaration_1()); 
                    // InternalMediaFlow.g:426:3: ( 'AUDIO' )
                    // InternalMediaFlow.g:426:4: 'AUDIO'
                    {
                    match(input,14,FOLLOW_2); 

                    }

                     after(grammarAccess.getMediaTypeAccess().getAUDIOEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalMediaFlow.g:430:2: ( ( 'SUBTITLE' ) )
                    {
                    // InternalMediaFlow.g:430:2: ( ( 'SUBTITLE' ) )
                    // InternalMediaFlow.g:431:3: ( 'SUBTITLE' )
                    {
                     before(grammarAccess.getMediaTypeAccess().getSUBTITLEEnumLiteralDeclaration_2()); 
                    // InternalMediaFlow.g:432:3: ( 'SUBTITLE' )
                    // InternalMediaFlow.g:432:4: 'SUBTITLE'
                    {
                    match(input,15,FOLLOW_2); 

                    }

                     after(grammarAccess.getMediaTypeAccess().getSUBTITLEEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalMediaFlow.g:436:2: ( ( 'IMAGE' ) )
                    {
                    // InternalMediaFlow.g:436:2: ( ( 'IMAGE' ) )
                    // InternalMediaFlow.g:437:3: ( 'IMAGE' )
                    {
                     before(grammarAccess.getMediaTypeAccess().getIMAGEEnumLiteralDeclaration_3()); 
                    // InternalMediaFlow.g:438:3: ( 'IMAGE' )
                    // InternalMediaFlow.g:438:4: 'IMAGE'
                    {
                    match(input,16,FOLLOW_2); 

                    }

                     after(grammarAccess.getMediaTypeAccess().getIMAGEEnumLiteralDeclaration_3()); 

                    }


                    }
                    break;
                case 5 :
                    // InternalMediaFlow.g:442:2: ( ( 'IMAGE_SEQUENCE' ) )
                    {
                    // InternalMediaFlow.g:442:2: ( ( 'IMAGE_SEQUENCE' ) )
                    // InternalMediaFlow.g:443:3: ( 'IMAGE_SEQUENCE' )
                    {
                     before(grammarAccess.getMediaTypeAccess().getIMAGE_SEQUENCEEnumLiteralDeclaration_4()); 
                    // InternalMediaFlow.g:444:3: ( 'IMAGE_SEQUENCE' )
                    // InternalMediaFlow.g:444:4: 'IMAGE_SEQUENCE'
                    {
                    match(input,17,FOLLOW_2); 

                    }

                     after(grammarAccess.getMediaTypeAccess().getIMAGE_SEQUENCEEnumLiteralDeclaration_4()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MediaType__Alternatives"


    // $ANTLR start "rule__Backend__Alternatives"
    // InternalMediaFlow.g:452:1: rule__Backend__Alternatives : ( ( ( 'FFMPEG' ) ) | ( ( 'GSTREAMER' ) ) | ( ( 'CUSTOM' ) ) );
    public final void rule__Backend__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:456:1: ( ( ( 'FFMPEG' ) ) | ( ( 'GSTREAMER' ) ) | ( ( 'CUSTOM' ) ) )
            int alt5=3;
            switch ( input.LA(1) ) {
            case 18:
                {
                alt5=1;
                }
                break;
            case 19:
                {
                alt5=2;
                }
                break;
            case 20:
                {
                alt5=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // InternalMediaFlow.g:457:2: ( ( 'FFMPEG' ) )
                    {
                    // InternalMediaFlow.g:457:2: ( ( 'FFMPEG' ) )
                    // InternalMediaFlow.g:458:3: ( 'FFMPEG' )
                    {
                     before(grammarAccess.getBackendAccess().getFFMPEGEnumLiteralDeclaration_0()); 
                    // InternalMediaFlow.g:459:3: ( 'FFMPEG' )
                    // InternalMediaFlow.g:459:4: 'FFMPEG'
                    {
                    match(input,18,FOLLOW_2); 

                    }

                     after(grammarAccess.getBackendAccess().getFFMPEGEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:463:2: ( ( 'GSTREAMER' ) )
                    {
                    // InternalMediaFlow.g:463:2: ( ( 'GSTREAMER' ) )
                    // InternalMediaFlow.g:464:3: ( 'GSTREAMER' )
                    {
                     before(grammarAccess.getBackendAccess().getGSTREAMEREnumLiteralDeclaration_1()); 
                    // InternalMediaFlow.g:465:3: ( 'GSTREAMER' )
                    // InternalMediaFlow.g:465:4: 'GSTREAMER'
                    {
                    match(input,19,FOLLOW_2); 

                    }

                     after(grammarAccess.getBackendAccess().getGSTREAMEREnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalMediaFlow.g:469:2: ( ( 'CUSTOM' ) )
                    {
                    // InternalMediaFlow.g:469:2: ( ( 'CUSTOM' ) )
                    // InternalMediaFlow.g:470:3: ( 'CUSTOM' )
                    {
                     before(grammarAccess.getBackendAccess().getCUSTOMEnumLiteralDeclaration_2()); 
                    // InternalMediaFlow.g:471:3: ( 'CUSTOM' )
                    // InternalMediaFlow.g:471:4: 'CUSTOM'
                    {
                    match(input,20,FOLLOW_2); 

                    }

                     after(grammarAccess.getBackendAccess().getCUSTOMEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Backend__Alternatives"


    // $ANTLR start "rule__Direction__Alternatives"
    // InternalMediaFlow.g:479:1: rule__Direction__Alternatives : ( ( ( 'IN' ) ) | ( ( 'OUT' ) ) );
    public final void rule__Direction__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:483:1: ( ( ( 'IN' ) ) | ( ( 'OUT' ) ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==21) ) {
                alt6=1;
            }
            else if ( (LA6_0==22) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // InternalMediaFlow.g:484:2: ( ( 'IN' ) )
                    {
                    // InternalMediaFlow.g:484:2: ( ( 'IN' ) )
                    // InternalMediaFlow.g:485:3: ( 'IN' )
                    {
                     before(grammarAccess.getDirectionAccess().getINEnumLiteralDeclaration_0()); 
                    // InternalMediaFlow.g:486:3: ( 'IN' )
                    // InternalMediaFlow.g:486:4: 'IN'
                    {
                    match(input,21,FOLLOW_2); 

                    }

                     after(grammarAccess.getDirectionAccess().getINEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:490:2: ( ( 'OUT' ) )
                    {
                    // InternalMediaFlow.g:490:2: ( ( 'OUT' ) )
                    // InternalMediaFlow.g:491:3: ( 'OUT' )
                    {
                     before(grammarAccess.getDirectionAccess().getOUTEnumLiteralDeclaration_1()); 
                    // InternalMediaFlow.g:492:3: ( 'OUT' )
                    // InternalMediaFlow.g:492:4: 'OUT'
                    {
                    match(input,22,FOLLOW_2); 

                    }

                     after(grammarAccess.getDirectionAccess().getOUTEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Direction__Alternatives"


    // $ANTLR start "rule__Graph__Group__0"
    // InternalMediaFlow.g:500:1: rule__Graph__Group__0 : rule__Graph__Group__0__Impl rule__Graph__Group__1 ;
    public final void rule__Graph__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:504:1: ( rule__Graph__Group__0__Impl rule__Graph__Group__1 )
            // InternalMediaFlow.g:505:2: rule__Graph__Group__0__Impl rule__Graph__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__Graph__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Graph__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__0"


    // $ANTLR start "rule__Graph__Group__0__Impl"
    // InternalMediaFlow.g:512:1: rule__Graph__Group__0__Impl : ( 'graph' ) ;
    public final void rule__Graph__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:516:1: ( ( 'graph' ) )
            // InternalMediaFlow.g:517:1: ( 'graph' )
            {
            // InternalMediaFlow.g:517:1: ( 'graph' )
            // InternalMediaFlow.g:518:2: 'graph'
            {
             before(grammarAccess.getGraphAccess().getGraphKeyword_0()); 
            match(input,23,FOLLOW_2); 
             after(grammarAccess.getGraphAccess().getGraphKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__0__Impl"


    // $ANTLR start "rule__Graph__Group__1"
    // InternalMediaFlow.g:527:1: rule__Graph__Group__1 : rule__Graph__Group__1__Impl rule__Graph__Group__2 ;
    public final void rule__Graph__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:531:1: ( rule__Graph__Group__1__Impl rule__Graph__Group__2 )
            // InternalMediaFlow.g:532:2: rule__Graph__Group__1__Impl rule__Graph__Group__2
            {
            pushFollow(FOLLOW_4);
            rule__Graph__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Graph__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__1"


    // $ANTLR start "rule__Graph__Group__1__Impl"
    // InternalMediaFlow.g:539:1: rule__Graph__Group__1__Impl : ( ( rule__Graph__NameAssignment_1 ) ) ;
    public final void rule__Graph__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:543:1: ( ( ( rule__Graph__NameAssignment_1 ) ) )
            // InternalMediaFlow.g:544:1: ( ( rule__Graph__NameAssignment_1 ) )
            {
            // InternalMediaFlow.g:544:1: ( ( rule__Graph__NameAssignment_1 ) )
            // InternalMediaFlow.g:545:2: ( rule__Graph__NameAssignment_1 )
            {
             before(grammarAccess.getGraphAccess().getNameAssignment_1()); 
            // InternalMediaFlow.g:546:2: ( rule__Graph__NameAssignment_1 )
            // InternalMediaFlow.g:546:3: rule__Graph__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Graph__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getGraphAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__1__Impl"


    // $ANTLR start "rule__Graph__Group__2"
    // InternalMediaFlow.g:554:1: rule__Graph__Group__2 : rule__Graph__Group__2__Impl rule__Graph__Group__3 ;
    public final void rule__Graph__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:558:1: ( rule__Graph__Group__2__Impl rule__Graph__Group__3 )
            // InternalMediaFlow.g:559:2: rule__Graph__Group__2__Impl rule__Graph__Group__3
            {
            pushFollow(FOLLOW_5);
            rule__Graph__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Graph__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__2"


    // $ANTLR start "rule__Graph__Group__2__Impl"
    // InternalMediaFlow.g:566:1: rule__Graph__Group__2__Impl : ( '{' ) ;
    public final void rule__Graph__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:570:1: ( ( '{' ) )
            // InternalMediaFlow.g:571:1: ( '{' )
            {
            // InternalMediaFlow.g:571:1: ( '{' )
            // InternalMediaFlow.g:572:2: '{'
            {
             before(grammarAccess.getGraphAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getGraphAccess().getLeftCurlyBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__2__Impl"


    // $ANTLR start "rule__Graph__Group__3"
    // InternalMediaFlow.g:581:1: rule__Graph__Group__3 : rule__Graph__Group__3__Impl rule__Graph__Group__4 ;
    public final void rule__Graph__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:585:1: ( rule__Graph__Group__3__Impl rule__Graph__Group__4 )
            // InternalMediaFlow.g:586:2: rule__Graph__Group__3__Impl rule__Graph__Group__4
            {
            pushFollow(FOLLOW_5);
            rule__Graph__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Graph__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__3"


    // $ANTLR start "rule__Graph__Group__3__Impl"
    // InternalMediaFlow.g:593:1: rule__Graph__Group__3__Impl : ( ( rule__Graph__NodesAssignment_3 )* ) ;
    public final void rule__Graph__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:597:1: ( ( ( rule__Graph__NodesAssignment_3 )* ) )
            // InternalMediaFlow.g:598:1: ( ( rule__Graph__NodesAssignment_3 )* )
            {
            // InternalMediaFlow.g:598:1: ( ( rule__Graph__NodesAssignment_3 )* )
            // InternalMediaFlow.g:599:2: ( rule__Graph__NodesAssignment_3 )*
            {
             before(grammarAccess.getGraphAccess().getNodesAssignment_3()); 
            // InternalMediaFlow.g:600:2: ( rule__Graph__NodesAssignment_3 )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==26||LA7_0==33||LA7_0==39) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // InternalMediaFlow.g:600:3: rule__Graph__NodesAssignment_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__Graph__NodesAssignment_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

             after(grammarAccess.getGraphAccess().getNodesAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__3__Impl"


    // $ANTLR start "rule__Graph__Group__4"
    // InternalMediaFlow.g:608:1: rule__Graph__Group__4 : rule__Graph__Group__4__Impl rule__Graph__Group__5 ;
    public final void rule__Graph__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:612:1: ( rule__Graph__Group__4__Impl rule__Graph__Group__5 )
            // InternalMediaFlow.g:613:2: rule__Graph__Group__4__Impl rule__Graph__Group__5
            {
            pushFollow(FOLLOW_5);
            rule__Graph__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Graph__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__4"


    // $ANTLR start "rule__Graph__Group__4__Impl"
    // InternalMediaFlow.g:620:1: rule__Graph__Group__4__Impl : ( ( rule__Graph__EdgesAssignment_4 )* ) ;
    public final void rule__Graph__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:624:1: ( ( ( rule__Graph__EdgesAssignment_4 )* ) )
            // InternalMediaFlow.g:625:1: ( ( rule__Graph__EdgesAssignment_4 )* )
            {
            // InternalMediaFlow.g:625:1: ( ( rule__Graph__EdgesAssignment_4 )* )
            // InternalMediaFlow.g:626:2: ( rule__Graph__EdgesAssignment_4 )*
            {
             before(grammarAccess.getGraphAccess().getEdgesAssignment_4()); 
            // InternalMediaFlow.g:627:2: ( rule__Graph__EdgesAssignment_4 )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==45) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // InternalMediaFlow.g:627:3: rule__Graph__EdgesAssignment_4
            	    {
            	    pushFollow(FOLLOW_7);
            	    rule__Graph__EdgesAssignment_4();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

             after(grammarAccess.getGraphAccess().getEdgesAssignment_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__4__Impl"


    // $ANTLR start "rule__Graph__Group__5"
    // InternalMediaFlow.g:635:1: rule__Graph__Group__5 : rule__Graph__Group__5__Impl ;
    public final void rule__Graph__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:639:1: ( rule__Graph__Group__5__Impl )
            // InternalMediaFlow.g:640:2: rule__Graph__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Graph__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__5"


    // $ANTLR start "rule__Graph__Group__5__Impl"
    // InternalMediaFlow.g:646:1: rule__Graph__Group__5__Impl : ( '}' ) ;
    public final void rule__Graph__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:650:1: ( ( '}' ) )
            // InternalMediaFlow.g:651:1: ( '}' )
            {
            // InternalMediaFlow.g:651:1: ( '}' )
            // InternalMediaFlow.g:652:2: '}'
            {
             before(grammarAccess.getGraphAccess().getRightCurlyBracketKeyword_5()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getGraphAccess().getRightCurlyBracketKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__Group__5__Impl"


    // $ANTLR start "rule__Resource__Group__0"
    // InternalMediaFlow.g:662:1: rule__Resource__Group__0 : rule__Resource__Group__0__Impl rule__Resource__Group__1 ;
    public final void rule__Resource__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:666:1: ( rule__Resource__Group__0__Impl rule__Resource__Group__1 )
            // InternalMediaFlow.g:667:2: rule__Resource__Group__0__Impl rule__Resource__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__Resource__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__0"


    // $ANTLR start "rule__Resource__Group__0__Impl"
    // InternalMediaFlow.g:674:1: rule__Resource__Group__0__Impl : ( 'resource' ) ;
    public final void rule__Resource__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:678:1: ( ( 'resource' ) )
            // InternalMediaFlow.g:679:1: ( 'resource' )
            {
            // InternalMediaFlow.g:679:1: ( 'resource' )
            // InternalMediaFlow.g:680:2: 'resource'
            {
             before(grammarAccess.getResourceAccess().getResourceKeyword_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getResourceKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__0__Impl"


    // $ANTLR start "rule__Resource__Group__1"
    // InternalMediaFlow.g:689:1: rule__Resource__Group__1 : rule__Resource__Group__1__Impl rule__Resource__Group__2 ;
    public final void rule__Resource__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:693:1: ( rule__Resource__Group__1__Impl rule__Resource__Group__2 )
            // InternalMediaFlow.g:694:2: rule__Resource__Group__1__Impl rule__Resource__Group__2
            {
            pushFollow(FOLLOW_8);
            rule__Resource__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__1"


    // $ANTLR start "rule__Resource__Group__1__Impl"
    // InternalMediaFlow.g:701:1: rule__Resource__Group__1__Impl : ( ( rule__Resource__NameAssignment_1 ) ) ;
    public final void rule__Resource__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:705:1: ( ( ( rule__Resource__NameAssignment_1 ) ) )
            // InternalMediaFlow.g:706:1: ( ( rule__Resource__NameAssignment_1 ) )
            {
            // InternalMediaFlow.g:706:1: ( ( rule__Resource__NameAssignment_1 ) )
            // InternalMediaFlow.g:707:2: ( rule__Resource__NameAssignment_1 )
            {
             before(grammarAccess.getResourceAccess().getNameAssignment_1()); 
            // InternalMediaFlow.g:708:2: ( rule__Resource__NameAssignment_1 )
            // InternalMediaFlow.g:708:3: rule__Resource__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Resource__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getResourceAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__1__Impl"


    // $ANTLR start "rule__Resource__Group__2"
    // InternalMediaFlow.g:716:1: rule__Resource__Group__2 : rule__Resource__Group__2__Impl rule__Resource__Group__3 ;
    public final void rule__Resource__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:720:1: ( rule__Resource__Group__2__Impl rule__Resource__Group__3 )
            // InternalMediaFlow.g:721:2: rule__Resource__Group__2__Impl rule__Resource__Group__3
            {
            pushFollow(FOLLOW_9);
            rule__Resource__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__2"


    // $ANTLR start "rule__Resource__Group__2__Impl"
    // InternalMediaFlow.g:728:1: rule__Resource__Group__2__Impl : ( '[' ) ;
    public final void rule__Resource__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:732:1: ( ( '[' ) )
            // InternalMediaFlow.g:733:1: ( '[' )
            {
            // InternalMediaFlow.g:733:1: ( '[' )
            // InternalMediaFlow.g:734:2: '['
            {
             before(grammarAccess.getResourceAccess().getLeftSquareBracketKeyword_2()); 
            match(input,27,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getLeftSquareBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__2__Impl"


    // $ANTLR start "rule__Resource__Group__3"
    // InternalMediaFlow.g:743:1: rule__Resource__Group__3 : rule__Resource__Group__3__Impl rule__Resource__Group__4 ;
    public final void rule__Resource__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:747:1: ( rule__Resource__Group__3__Impl rule__Resource__Group__4 )
            // InternalMediaFlow.g:748:2: rule__Resource__Group__3__Impl rule__Resource__Group__4
            {
            pushFollow(FOLLOW_10);
            rule__Resource__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__3"


    // $ANTLR start "rule__Resource__Group__3__Impl"
    // InternalMediaFlow.g:755:1: rule__Resource__Group__3__Impl : ( 'mediaType' ) ;
    public final void rule__Resource__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:759:1: ( ( 'mediaType' ) )
            // InternalMediaFlow.g:760:1: ( 'mediaType' )
            {
            // InternalMediaFlow.g:760:1: ( 'mediaType' )
            // InternalMediaFlow.g:761:2: 'mediaType'
            {
             before(grammarAccess.getResourceAccess().getMediaTypeKeyword_3()); 
            match(input,28,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getMediaTypeKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__3__Impl"


    // $ANTLR start "rule__Resource__Group__4"
    // InternalMediaFlow.g:770:1: rule__Resource__Group__4 : rule__Resource__Group__4__Impl rule__Resource__Group__5 ;
    public final void rule__Resource__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:774:1: ( rule__Resource__Group__4__Impl rule__Resource__Group__5 )
            // InternalMediaFlow.g:775:2: rule__Resource__Group__4__Impl rule__Resource__Group__5
            {
            pushFollow(FOLLOW_11);
            rule__Resource__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__4"


    // $ANTLR start "rule__Resource__Group__4__Impl"
    // InternalMediaFlow.g:782:1: rule__Resource__Group__4__Impl : ( '=' ) ;
    public final void rule__Resource__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:786:1: ( ( '=' ) )
            // InternalMediaFlow.g:787:1: ( '=' )
            {
            // InternalMediaFlow.g:787:1: ( '=' )
            // InternalMediaFlow.g:788:2: '='
            {
             before(grammarAccess.getResourceAccess().getEqualsSignKeyword_4()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getEqualsSignKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__4__Impl"


    // $ANTLR start "rule__Resource__Group__5"
    // InternalMediaFlow.g:797:1: rule__Resource__Group__5 : rule__Resource__Group__5__Impl rule__Resource__Group__6 ;
    public final void rule__Resource__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:801:1: ( rule__Resource__Group__5__Impl rule__Resource__Group__6 )
            // InternalMediaFlow.g:802:2: rule__Resource__Group__5__Impl rule__Resource__Group__6
            {
            pushFollow(FOLLOW_12);
            rule__Resource__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__5"


    // $ANTLR start "rule__Resource__Group__5__Impl"
    // InternalMediaFlow.g:809:1: rule__Resource__Group__5__Impl : ( ( rule__Resource__MediaTypeAssignment_5 ) ) ;
    public final void rule__Resource__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:813:1: ( ( ( rule__Resource__MediaTypeAssignment_5 ) ) )
            // InternalMediaFlow.g:814:1: ( ( rule__Resource__MediaTypeAssignment_5 ) )
            {
            // InternalMediaFlow.g:814:1: ( ( rule__Resource__MediaTypeAssignment_5 ) )
            // InternalMediaFlow.g:815:2: ( rule__Resource__MediaTypeAssignment_5 )
            {
             before(grammarAccess.getResourceAccess().getMediaTypeAssignment_5()); 
            // InternalMediaFlow.g:816:2: ( rule__Resource__MediaTypeAssignment_5 )
            // InternalMediaFlow.g:816:3: rule__Resource__MediaTypeAssignment_5
            {
            pushFollow(FOLLOW_2);
            rule__Resource__MediaTypeAssignment_5();

            state._fsp--;


            }

             after(grammarAccess.getResourceAccess().getMediaTypeAssignment_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__5__Impl"


    // $ANTLR start "rule__Resource__Group__6"
    // InternalMediaFlow.g:824:1: rule__Resource__Group__6 : rule__Resource__Group__6__Impl rule__Resource__Group__7 ;
    public final void rule__Resource__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:828:1: ( rule__Resource__Group__6__Impl rule__Resource__Group__7 )
            // InternalMediaFlow.g:829:2: rule__Resource__Group__6__Impl rule__Resource__Group__7
            {
            pushFollow(FOLLOW_13);
            rule__Resource__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__6"


    // $ANTLR start "rule__Resource__Group__6__Impl"
    // InternalMediaFlow.g:836:1: rule__Resource__Group__6__Impl : ( ']' ) ;
    public final void rule__Resource__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:840:1: ( ( ']' ) )
            // InternalMediaFlow.g:841:1: ( ']' )
            {
            // InternalMediaFlow.g:841:1: ( ']' )
            // InternalMediaFlow.g:842:2: ']'
            {
             before(grammarAccess.getResourceAccess().getRightSquareBracketKeyword_6()); 
            match(input,30,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getRightSquareBracketKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__6__Impl"


    // $ANTLR start "rule__Resource__Group__7"
    // InternalMediaFlow.g:851:1: rule__Resource__Group__7 : rule__Resource__Group__7__Impl rule__Resource__Group__8 ;
    public final void rule__Resource__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:855:1: ( rule__Resource__Group__7__Impl rule__Resource__Group__8 )
            // InternalMediaFlow.g:856:2: rule__Resource__Group__7__Impl rule__Resource__Group__8
            {
            pushFollow(FOLLOW_10);
            rule__Resource__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__7"


    // $ANTLR start "rule__Resource__Group__7__Impl"
    // InternalMediaFlow.g:863:1: rule__Resource__Group__7__Impl : ( 'uri' ) ;
    public final void rule__Resource__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:867:1: ( ( 'uri' ) )
            // InternalMediaFlow.g:868:1: ( 'uri' )
            {
            // InternalMediaFlow.g:868:1: ( 'uri' )
            // InternalMediaFlow.g:869:2: 'uri'
            {
             before(grammarAccess.getResourceAccess().getUriKeyword_7()); 
            match(input,31,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getUriKeyword_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__7__Impl"


    // $ANTLR start "rule__Resource__Group__8"
    // InternalMediaFlow.g:878:1: rule__Resource__Group__8 : rule__Resource__Group__8__Impl rule__Resource__Group__9 ;
    public final void rule__Resource__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:882:1: ( rule__Resource__Group__8__Impl rule__Resource__Group__9 )
            // InternalMediaFlow.g:883:2: rule__Resource__Group__8__Impl rule__Resource__Group__9
            {
            pushFollow(FOLLOW_14);
            rule__Resource__Group__8__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__9();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__8"


    // $ANTLR start "rule__Resource__Group__8__Impl"
    // InternalMediaFlow.g:890:1: rule__Resource__Group__8__Impl : ( '=' ) ;
    public final void rule__Resource__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:894:1: ( ( '=' ) )
            // InternalMediaFlow.g:895:1: ( '=' )
            {
            // InternalMediaFlow.g:895:1: ( '=' )
            // InternalMediaFlow.g:896:2: '='
            {
             before(grammarAccess.getResourceAccess().getEqualsSignKeyword_8()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getEqualsSignKeyword_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__8__Impl"


    // $ANTLR start "rule__Resource__Group__9"
    // InternalMediaFlow.g:905:1: rule__Resource__Group__9 : rule__Resource__Group__9__Impl rule__Resource__Group__10 ;
    public final void rule__Resource__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:909:1: ( rule__Resource__Group__9__Impl rule__Resource__Group__10 )
            // InternalMediaFlow.g:910:2: rule__Resource__Group__9__Impl rule__Resource__Group__10
            {
            pushFollow(FOLLOW_15);
            rule__Resource__Group__9__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__10();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__9"


    // $ANTLR start "rule__Resource__Group__9__Impl"
    // InternalMediaFlow.g:917:1: rule__Resource__Group__9__Impl : ( ( rule__Resource__UriAssignment_9 ) ) ;
    public final void rule__Resource__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:921:1: ( ( ( rule__Resource__UriAssignment_9 ) ) )
            // InternalMediaFlow.g:922:1: ( ( rule__Resource__UriAssignment_9 ) )
            {
            // InternalMediaFlow.g:922:1: ( ( rule__Resource__UriAssignment_9 ) )
            // InternalMediaFlow.g:923:2: ( rule__Resource__UriAssignment_9 )
            {
             before(grammarAccess.getResourceAccess().getUriAssignment_9()); 
            // InternalMediaFlow.g:924:2: ( rule__Resource__UriAssignment_9 )
            // InternalMediaFlow.g:924:3: rule__Resource__UriAssignment_9
            {
            pushFollow(FOLLOW_2);
            rule__Resource__UriAssignment_9();

            state._fsp--;


            }

             after(grammarAccess.getResourceAccess().getUriAssignment_9()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__9__Impl"


    // $ANTLR start "rule__Resource__Group__10"
    // InternalMediaFlow.g:932:1: rule__Resource__Group__10 : rule__Resource__Group__10__Impl rule__Resource__Group__11 ;
    public final void rule__Resource__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:936:1: ( rule__Resource__Group__10__Impl rule__Resource__Group__11 )
            // InternalMediaFlow.g:937:2: rule__Resource__Group__10__Impl rule__Resource__Group__11
            {
            pushFollow(FOLLOW_10);
            rule__Resource__Group__10__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__11();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__10"


    // $ANTLR start "rule__Resource__Group__10__Impl"
    // InternalMediaFlow.g:944:1: rule__Resource__Group__10__Impl : ( 'external' ) ;
    public final void rule__Resource__Group__10__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:948:1: ( ( 'external' ) )
            // InternalMediaFlow.g:949:1: ( 'external' )
            {
            // InternalMediaFlow.g:949:1: ( 'external' )
            // InternalMediaFlow.g:950:2: 'external'
            {
             before(grammarAccess.getResourceAccess().getExternalKeyword_10()); 
            match(input,32,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getExternalKeyword_10()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__10__Impl"


    // $ANTLR start "rule__Resource__Group__11"
    // InternalMediaFlow.g:959:1: rule__Resource__Group__11 : rule__Resource__Group__11__Impl rule__Resource__Group__12 ;
    public final void rule__Resource__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:963:1: ( rule__Resource__Group__11__Impl rule__Resource__Group__12 )
            // InternalMediaFlow.g:964:2: rule__Resource__Group__11__Impl rule__Resource__Group__12
            {
            pushFollow(FOLLOW_16);
            rule__Resource__Group__11__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__12();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__11"


    // $ANTLR start "rule__Resource__Group__11__Impl"
    // InternalMediaFlow.g:971:1: rule__Resource__Group__11__Impl : ( '=' ) ;
    public final void rule__Resource__Group__11__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:975:1: ( ( '=' ) )
            // InternalMediaFlow.g:976:1: ( '=' )
            {
            // InternalMediaFlow.g:976:1: ( '=' )
            // InternalMediaFlow.g:977:2: '='
            {
             before(grammarAccess.getResourceAccess().getEqualsSignKeyword_11()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getEqualsSignKeyword_11()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__11__Impl"


    // $ANTLR start "rule__Resource__Group__12"
    // InternalMediaFlow.g:986:1: rule__Resource__Group__12 : rule__Resource__Group__12__Impl rule__Resource__Group__13 ;
    public final void rule__Resource__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:990:1: ( rule__Resource__Group__12__Impl rule__Resource__Group__13 )
            // InternalMediaFlow.g:991:2: rule__Resource__Group__12__Impl rule__Resource__Group__13
            {
            pushFollow(FOLLOW_4);
            rule__Resource__Group__12__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__13();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__12"


    // $ANTLR start "rule__Resource__Group__12__Impl"
    // InternalMediaFlow.g:998:1: rule__Resource__Group__12__Impl : ( ( rule__Resource__ExternalAssignment_12 ) ) ;
    public final void rule__Resource__Group__12__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1002:1: ( ( ( rule__Resource__ExternalAssignment_12 ) ) )
            // InternalMediaFlow.g:1003:1: ( ( rule__Resource__ExternalAssignment_12 ) )
            {
            // InternalMediaFlow.g:1003:1: ( ( rule__Resource__ExternalAssignment_12 ) )
            // InternalMediaFlow.g:1004:2: ( rule__Resource__ExternalAssignment_12 )
            {
             before(grammarAccess.getResourceAccess().getExternalAssignment_12()); 
            // InternalMediaFlow.g:1005:2: ( rule__Resource__ExternalAssignment_12 )
            // InternalMediaFlow.g:1005:3: rule__Resource__ExternalAssignment_12
            {
            pushFollow(FOLLOW_2);
            rule__Resource__ExternalAssignment_12();

            state._fsp--;


            }

             after(grammarAccess.getResourceAccess().getExternalAssignment_12()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__12__Impl"


    // $ANTLR start "rule__Resource__Group__13"
    // InternalMediaFlow.g:1013:1: rule__Resource__Group__13 : rule__Resource__Group__13__Impl rule__Resource__Group__14 ;
    public final void rule__Resource__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1017:1: ( rule__Resource__Group__13__Impl rule__Resource__Group__14 )
            // InternalMediaFlow.g:1018:2: rule__Resource__Group__13__Impl rule__Resource__Group__14
            {
            pushFollow(FOLLOW_17);
            rule__Resource__Group__13__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__14();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__13"


    // $ANTLR start "rule__Resource__Group__13__Impl"
    // InternalMediaFlow.g:1025:1: rule__Resource__Group__13__Impl : ( '{' ) ;
    public final void rule__Resource__Group__13__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1029:1: ( ( '{' ) )
            // InternalMediaFlow.g:1030:1: ( '{' )
            {
            // InternalMediaFlow.g:1030:1: ( '{' )
            // InternalMediaFlow.g:1031:2: '{'
            {
             before(grammarAccess.getResourceAccess().getLeftCurlyBracketKeyword_13()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getLeftCurlyBracketKeyword_13()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__13__Impl"


    // $ANTLR start "rule__Resource__Group__14"
    // InternalMediaFlow.g:1040:1: rule__Resource__Group__14 : rule__Resource__Group__14__Impl rule__Resource__Group__15 ;
    public final void rule__Resource__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1044:1: ( rule__Resource__Group__14__Impl rule__Resource__Group__15 )
            // InternalMediaFlow.g:1045:2: rule__Resource__Group__14__Impl rule__Resource__Group__15
            {
            pushFollow(FOLLOW_17);
            rule__Resource__Group__14__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Resource__Group__15();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__14"


    // $ANTLR start "rule__Resource__Group__14__Impl"
    // InternalMediaFlow.g:1052:1: rule__Resource__Group__14__Impl : ( ( rule__Resource__PortsAssignment_14 )* ) ;
    public final void rule__Resource__Group__14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1056:1: ( ( ( rule__Resource__PortsAssignment_14 )* ) )
            // InternalMediaFlow.g:1057:1: ( ( rule__Resource__PortsAssignment_14 )* )
            {
            // InternalMediaFlow.g:1057:1: ( ( rule__Resource__PortsAssignment_14 )* )
            // InternalMediaFlow.g:1058:2: ( rule__Resource__PortsAssignment_14 )*
            {
             before(grammarAccess.getResourceAccess().getPortsAssignment_14()); 
            // InternalMediaFlow.g:1059:2: ( rule__Resource__PortsAssignment_14 )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==44) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // InternalMediaFlow.g:1059:3: rule__Resource__PortsAssignment_14
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__Resource__PortsAssignment_14();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

             after(grammarAccess.getResourceAccess().getPortsAssignment_14()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__14__Impl"


    // $ANTLR start "rule__Resource__Group__15"
    // InternalMediaFlow.g:1067:1: rule__Resource__Group__15 : rule__Resource__Group__15__Impl ;
    public final void rule__Resource__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1071:1: ( rule__Resource__Group__15__Impl )
            // InternalMediaFlow.g:1072:2: rule__Resource__Group__15__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Resource__Group__15__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__15"


    // $ANTLR start "rule__Resource__Group__15__Impl"
    // InternalMediaFlow.g:1078:1: rule__Resource__Group__15__Impl : ( '}' ) ;
    public final void rule__Resource__Group__15__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1082:1: ( ( '}' ) )
            // InternalMediaFlow.g:1083:1: ( '}' )
            {
            // InternalMediaFlow.g:1083:1: ( '}' )
            // InternalMediaFlow.g:1084:2: '}'
            {
             before(grammarAccess.getResourceAccess().getRightCurlyBracketKeyword_15()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getRightCurlyBracketKeyword_15()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__Group__15__Impl"


    // $ANTLR start "rule__Scaler__Group__0"
    // InternalMediaFlow.g:1094:1: rule__Scaler__Group__0 : rule__Scaler__Group__0__Impl rule__Scaler__Group__1 ;
    public final void rule__Scaler__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1098:1: ( rule__Scaler__Group__0__Impl rule__Scaler__Group__1 )
            // InternalMediaFlow.g:1099:2: rule__Scaler__Group__0__Impl rule__Scaler__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__Scaler__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__0"


    // $ANTLR start "rule__Scaler__Group__0__Impl"
    // InternalMediaFlow.g:1106:1: rule__Scaler__Group__0__Impl : ( 'scaler' ) ;
    public final void rule__Scaler__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1110:1: ( ( 'scaler' ) )
            // InternalMediaFlow.g:1111:1: ( 'scaler' )
            {
            // InternalMediaFlow.g:1111:1: ( 'scaler' )
            // InternalMediaFlow.g:1112:2: 'scaler'
            {
             before(grammarAccess.getScalerAccess().getScalerKeyword_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getScalerKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__0__Impl"


    // $ANTLR start "rule__Scaler__Group__1"
    // InternalMediaFlow.g:1121:1: rule__Scaler__Group__1 : rule__Scaler__Group__1__Impl rule__Scaler__Group__2 ;
    public final void rule__Scaler__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1125:1: ( rule__Scaler__Group__1__Impl rule__Scaler__Group__2 )
            // InternalMediaFlow.g:1126:2: rule__Scaler__Group__1__Impl rule__Scaler__Group__2
            {
            pushFollow(FOLLOW_19);
            rule__Scaler__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__1"


    // $ANTLR start "rule__Scaler__Group__1__Impl"
    // InternalMediaFlow.g:1133:1: rule__Scaler__Group__1__Impl : ( ( rule__Scaler__NameAssignment_1 ) ) ;
    public final void rule__Scaler__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1137:1: ( ( ( rule__Scaler__NameAssignment_1 ) ) )
            // InternalMediaFlow.g:1138:1: ( ( rule__Scaler__NameAssignment_1 ) )
            {
            // InternalMediaFlow.g:1138:1: ( ( rule__Scaler__NameAssignment_1 ) )
            // InternalMediaFlow.g:1139:2: ( rule__Scaler__NameAssignment_1 )
            {
             before(grammarAccess.getScalerAccess().getNameAssignment_1()); 
            // InternalMediaFlow.g:1140:2: ( rule__Scaler__NameAssignment_1 )
            // InternalMediaFlow.g:1140:3: rule__Scaler__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getScalerAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__1__Impl"


    // $ANTLR start "rule__Scaler__Group__2"
    // InternalMediaFlow.g:1148:1: rule__Scaler__Group__2 : rule__Scaler__Group__2__Impl rule__Scaler__Group__3 ;
    public final void rule__Scaler__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1152:1: ( rule__Scaler__Group__2__Impl rule__Scaler__Group__3 )
            // InternalMediaFlow.g:1153:2: rule__Scaler__Group__2__Impl rule__Scaler__Group__3
            {
            pushFollow(FOLLOW_10);
            rule__Scaler__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__2"


    // $ANTLR start "rule__Scaler__Group__2__Impl"
    // InternalMediaFlow.g:1160:1: rule__Scaler__Group__2__Impl : ( 'backend' ) ;
    public final void rule__Scaler__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1164:1: ( ( 'backend' ) )
            // InternalMediaFlow.g:1165:1: ( 'backend' )
            {
            // InternalMediaFlow.g:1165:1: ( 'backend' )
            // InternalMediaFlow.g:1166:2: 'backend'
            {
             before(grammarAccess.getScalerAccess().getBackendKeyword_2()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getBackendKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__2__Impl"


    // $ANTLR start "rule__Scaler__Group__3"
    // InternalMediaFlow.g:1175:1: rule__Scaler__Group__3 : rule__Scaler__Group__3__Impl rule__Scaler__Group__4 ;
    public final void rule__Scaler__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1179:1: ( rule__Scaler__Group__3__Impl rule__Scaler__Group__4 )
            // InternalMediaFlow.g:1180:2: rule__Scaler__Group__3__Impl rule__Scaler__Group__4
            {
            pushFollow(FOLLOW_20);
            rule__Scaler__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__3"


    // $ANTLR start "rule__Scaler__Group__3__Impl"
    // InternalMediaFlow.g:1187:1: rule__Scaler__Group__3__Impl : ( '=' ) ;
    public final void rule__Scaler__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1191:1: ( ( '=' ) )
            // InternalMediaFlow.g:1192:1: ( '=' )
            {
            // InternalMediaFlow.g:1192:1: ( '=' )
            // InternalMediaFlow.g:1193:2: '='
            {
             before(grammarAccess.getScalerAccess().getEqualsSignKeyword_3()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getEqualsSignKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__3__Impl"


    // $ANTLR start "rule__Scaler__Group__4"
    // InternalMediaFlow.g:1202:1: rule__Scaler__Group__4 : rule__Scaler__Group__4__Impl rule__Scaler__Group__5 ;
    public final void rule__Scaler__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1206:1: ( rule__Scaler__Group__4__Impl rule__Scaler__Group__5 )
            // InternalMediaFlow.g:1207:2: rule__Scaler__Group__4__Impl rule__Scaler__Group__5
            {
            pushFollow(FOLLOW_21);
            rule__Scaler__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__4"


    // $ANTLR start "rule__Scaler__Group__4__Impl"
    // InternalMediaFlow.g:1214:1: rule__Scaler__Group__4__Impl : ( ( rule__Scaler__BackendAssignment_4 ) ) ;
    public final void rule__Scaler__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1218:1: ( ( ( rule__Scaler__BackendAssignment_4 ) ) )
            // InternalMediaFlow.g:1219:1: ( ( rule__Scaler__BackendAssignment_4 ) )
            {
            // InternalMediaFlow.g:1219:1: ( ( rule__Scaler__BackendAssignment_4 ) )
            // InternalMediaFlow.g:1220:2: ( rule__Scaler__BackendAssignment_4 )
            {
             before(grammarAccess.getScalerAccess().getBackendAssignment_4()); 
            // InternalMediaFlow.g:1221:2: ( rule__Scaler__BackendAssignment_4 )
            // InternalMediaFlow.g:1221:3: rule__Scaler__BackendAssignment_4
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__BackendAssignment_4();

            state._fsp--;


            }

             after(grammarAccess.getScalerAccess().getBackendAssignment_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__4__Impl"


    // $ANTLR start "rule__Scaler__Group__5"
    // InternalMediaFlow.g:1229:1: rule__Scaler__Group__5 : rule__Scaler__Group__5__Impl rule__Scaler__Group__6 ;
    public final void rule__Scaler__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1233:1: ( rule__Scaler__Group__5__Impl rule__Scaler__Group__6 )
            // InternalMediaFlow.g:1234:2: rule__Scaler__Group__5__Impl rule__Scaler__Group__6
            {
            pushFollow(FOLLOW_10);
            rule__Scaler__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__5"


    // $ANTLR start "rule__Scaler__Group__5__Impl"
    // InternalMediaFlow.g:1241:1: rule__Scaler__Group__5__Impl : ( 'command' ) ;
    public final void rule__Scaler__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1245:1: ( ( 'command' ) )
            // InternalMediaFlow.g:1246:1: ( 'command' )
            {
            // InternalMediaFlow.g:1246:1: ( 'command' )
            // InternalMediaFlow.g:1247:2: 'command'
            {
             before(grammarAccess.getScalerAccess().getCommandKeyword_5()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getCommandKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__5__Impl"


    // $ANTLR start "rule__Scaler__Group__6"
    // InternalMediaFlow.g:1256:1: rule__Scaler__Group__6 : rule__Scaler__Group__6__Impl rule__Scaler__Group__7 ;
    public final void rule__Scaler__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1260:1: ( rule__Scaler__Group__6__Impl rule__Scaler__Group__7 )
            // InternalMediaFlow.g:1261:2: rule__Scaler__Group__6__Impl rule__Scaler__Group__7
            {
            pushFollow(FOLLOW_14);
            rule__Scaler__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__6"


    // $ANTLR start "rule__Scaler__Group__6__Impl"
    // InternalMediaFlow.g:1268:1: rule__Scaler__Group__6__Impl : ( '=' ) ;
    public final void rule__Scaler__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1272:1: ( ( '=' ) )
            // InternalMediaFlow.g:1273:1: ( '=' )
            {
            // InternalMediaFlow.g:1273:1: ( '=' )
            // InternalMediaFlow.g:1274:2: '='
            {
             before(grammarAccess.getScalerAccess().getEqualsSignKeyword_6()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getEqualsSignKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__6__Impl"


    // $ANTLR start "rule__Scaler__Group__7"
    // InternalMediaFlow.g:1283:1: rule__Scaler__Group__7 : rule__Scaler__Group__7__Impl rule__Scaler__Group__8 ;
    public final void rule__Scaler__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1287:1: ( rule__Scaler__Group__7__Impl rule__Scaler__Group__8 )
            // InternalMediaFlow.g:1288:2: rule__Scaler__Group__7__Impl rule__Scaler__Group__8
            {
            pushFollow(FOLLOW_22);
            rule__Scaler__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__7"


    // $ANTLR start "rule__Scaler__Group__7__Impl"
    // InternalMediaFlow.g:1295:1: rule__Scaler__Group__7__Impl : ( ( rule__Scaler__CommandAssignment_7 ) ) ;
    public final void rule__Scaler__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1299:1: ( ( ( rule__Scaler__CommandAssignment_7 ) ) )
            // InternalMediaFlow.g:1300:1: ( ( rule__Scaler__CommandAssignment_7 ) )
            {
            // InternalMediaFlow.g:1300:1: ( ( rule__Scaler__CommandAssignment_7 ) )
            // InternalMediaFlow.g:1301:2: ( rule__Scaler__CommandAssignment_7 )
            {
             before(grammarAccess.getScalerAccess().getCommandAssignment_7()); 
            // InternalMediaFlow.g:1302:2: ( rule__Scaler__CommandAssignment_7 )
            // InternalMediaFlow.g:1302:3: rule__Scaler__CommandAssignment_7
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__CommandAssignment_7();

            state._fsp--;


            }

             after(grammarAccess.getScalerAccess().getCommandAssignment_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__7__Impl"


    // $ANTLR start "rule__Scaler__Group__8"
    // InternalMediaFlow.g:1310:1: rule__Scaler__Group__8 : rule__Scaler__Group__8__Impl rule__Scaler__Group__9 ;
    public final void rule__Scaler__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1314:1: ( rule__Scaler__Group__8__Impl rule__Scaler__Group__9 )
            // InternalMediaFlow.g:1315:2: rule__Scaler__Group__8__Impl rule__Scaler__Group__9
            {
            pushFollow(FOLLOW_10);
            rule__Scaler__Group__8__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__9();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__8"


    // $ANTLR start "rule__Scaler__Group__8__Impl"
    // InternalMediaFlow.g:1322:1: rule__Scaler__Group__8__Impl : ( 'replicas' ) ;
    public final void rule__Scaler__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1326:1: ( ( 'replicas' ) )
            // InternalMediaFlow.g:1327:1: ( 'replicas' )
            {
            // InternalMediaFlow.g:1327:1: ( 'replicas' )
            // InternalMediaFlow.g:1328:2: 'replicas'
            {
             before(grammarAccess.getScalerAccess().getReplicasKeyword_8()); 
            match(input,36,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getReplicasKeyword_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__8__Impl"


    // $ANTLR start "rule__Scaler__Group__9"
    // InternalMediaFlow.g:1337:1: rule__Scaler__Group__9 : rule__Scaler__Group__9__Impl rule__Scaler__Group__10 ;
    public final void rule__Scaler__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1341:1: ( rule__Scaler__Group__9__Impl rule__Scaler__Group__10 )
            // InternalMediaFlow.g:1342:2: rule__Scaler__Group__9__Impl rule__Scaler__Group__10
            {
            pushFollow(FOLLOW_23);
            rule__Scaler__Group__9__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__10();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__9"


    // $ANTLR start "rule__Scaler__Group__9__Impl"
    // InternalMediaFlow.g:1349:1: rule__Scaler__Group__9__Impl : ( '=' ) ;
    public final void rule__Scaler__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1353:1: ( ( '=' ) )
            // InternalMediaFlow.g:1354:1: ( '=' )
            {
            // InternalMediaFlow.g:1354:1: ( '=' )
            // InternalMediaFlow.g:1355:2: '='
            {
             before(grammarAccess.getScalerAccess().getEqualsSignKeyword_9()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getEqualsSignKeyword_9()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__9__Impl"


    // $ANTLR start "rule__Scaler__Group__10"
    // InternalMediaFlow.g:1364:1: rule__Scaler__Group__10 : rule__Scaler__Group__10__Impl rule__Scaler__Group__11 ;
    public final void rule__Scaler__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1368:1: ( rule__Scaler__Group__10__Impl rule__Scaler__Group__11 )
            // InternalMediaFlow.g:1369:2: rule__Scaler__Group__10__Impl rule__Scaler__Group__11
            {
            pushFollow(FOLLOW_24);
            rule__Scaler__Group__10__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__11();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__10"


    // $ANTLR start "rule__Scaler__Group__10__Impl"
    // InternalMediaFlow.g:1376:1: rule__Scaler__Group__10__Impl : ( ( rule__Scaler__ReplicasAssignment_10 ) ) ;
    public final void rule__Scaler__Group__10__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1380:1: ( ( ( rule__Scaler__ReplicasAssignment_10 ) ) )
            // InternalMediaFlow.g:1381:1: ( ( rule__Scaler__ReplicasAssignment_10 ) )
            {
            // InternalMediaFlow.g:1381:1: ( ( rule__Scaler__ReplicasAssignment_10 ) )
            // InternalMediaFlow.g:1382:2: ( rule__Scaler__ReplicasAssignment_10 )
            {
             before(grammarAccess.getScalerAccess().getReplicasAssignment_10()); 
            // InternalMediaFlow.g:1383:2: ( rule__Scaler__ReplicasAssignment_10 )
            // InternalMediaFlow.g:1383:3: rule__Scaler__ReplicasAssignment_10
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__ReplicasAssignment_10();

            state._fsp--;


            }

             after(grammarAccess.getScalerAccess().getReplicasAssignment_10()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__10__Impl"


    // $ANTLR start "rule__Scaler__Group__11"
    // InternalMediaFlow.g:1391:1: rule__Scaler__Group__11 : rule__Scaler__Group__11__Impl rule__Scaler__Group__12 ;
    public final void rule__Scaler__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1395:1: ( rule__Scaler__Group__11__Impl rule__Scaler__Group__12 )
            // InternalMediaFlow.g:1396:2: rule__Scaler__Group__11__Impl rule__Scaler__Group__12
            {
            pushFollow(FOLLOW_10);
            rule__Scaler__Group__11__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__12();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__11"


    // $ANTLR start "rule__Scaler__Group__11__Impl"
    // InternalMediaFlow.g:1403:1: rule__Scaler__Group__11__Impl : ( 'width' ) ;
    public final void rule__Scaler__Group__11__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1407:1: ( ( 'width' ) )
            // InternalMediaFlow.g:1408:1: ( 'width' )
            {
            // InternalMediaFlow.g:1408:1: ( 'width' )
            // InternalMediaFlow.g:1409:2: 'width'
            {
             before(grammarAccess.getScalerAccess().getWidthKeyword_11()); 
            match(input,37,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getWidthKeyword_11()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__11__Impl"


    // $ANTLR start "rule__Scaler__Group__12"
    // InternalMediaFlow.g:1418:1: rule__Scaler__Group__12 : rule__Scaler__Group__12__Impl rule__Scaler__Group__13 ;
    public final void rule__Scaler__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1422:1: ( rule__Scaler__Group__12__Impl rule__Scaler__Group__13 )
            // InternalMediaFlow.g:1423:2: rule__Scaler__Group__12__Impl rule__Scaler__Group__13
            {
            pushFollow(FOLLOW_23);
            rule__Scaler__Group__12__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__13();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__12"


    // $ANTLR start "rule__Scaler__Group__12__Impl"
    // InternalMediaFlow.g:1430:1: rule__Scaler__Group__12__Impl : ( '=' ) ;
    public final void rule__Scaler__Group__12__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1434:1: ( ( '=' ) )
            // InternalMediaFlow.g:1435:1: ( '=' )
            {
            // InternalMediaFlow.g:1435:1: ( '=' )
            // InternalMediaFlow.g:1436:2: '='
            {
             before(grammarAccess.getScalerAccess().getEqualsSignKeyword_12()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getEqualsSignKeyword_12()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__12__Impl"


    // $ANTLR start "rule__Scaler__Group__13"
    // InternalMediaFlow.g:1445:1: rule__Scaler__Group__13 : rule__Scaler__Group__13__Impl rule__Scaler__Group__14 ;
    public final void rule__Scaler__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1449:1: ( rule__Scaler__Group__13__Impl rule__Scaler__Group__14 )
            // InternalMediaFlow.g:1450:2: rule__Scaler__Group__13__Impl rule__Scaler__Group__14
            {
            pushFollow(FOLLOW_25);
            rule__Scaler__Group__13__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__14();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__13"


    // $ANTLR start "rule__Scaler__Group__13__Impl"
    // InternalMediaFlow.g:1457:1: rule__Scaler__Group__13__Impl : ( ( rule__Scaler__WidthAssignment_13 ) ) ;
    public final void rule__Scaler__Group__13__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1461:1: ( ( ( rule__Scaler__WidthAssignment_13 ) ) )
            // InternalMediaFlow.g:1462:1: ( ( rule__Scaler__WidthAssignment_13 ) )
            {
            // InternalMediaFlow.g:1462:1: ( ( rule__Scaler__WidthAssignment_13 ) )
            // InternalMediaFlow.g:1463:2: ( rule__Scaler__WidthAssignment_13 )
            {
             before(grammarAccess.getScalerAccess().getWidthAssignment_13()); 
            // InternalMediaFlow.g:1464:2: ( rule__Scaler__WidthAssignment_13 )
            // InternalMediaFlow.g:1464:3: rule__Scaler__WidthAssignment_13
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__WidthAssignment_13();

            state._fsp--;


            }

             after(grammarAccess.getScalerAccess().getWidthAssignment_13()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__13__Impl"


    // $ANTLR start "rule__Scaler__Group__14"
    // InternalMediaFlow.g:1472:1: rule__Scaler__Group__14 : rule__Scaler__Group__14__Impl rule__Scaler__Group__15 ;
    public final void rule__Scaler__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1476:1: ( rule__Scaler__Group__14__Impl rule__Scaler__Group__15 )
            // InternalMediaFlow.g:1477:2: rule__Scaler__Group__14__Impl rule__Scaler__Group__15
            {
            pushFollow(FOLLOW_10);
            rule__Scaler__Group__14__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__15();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__14"


    // $ANTLR start "rule__Scaler__Group__14__Impl"
    // InternalMediaFlow.g:1484:1: rule__Scaler__Group__14__Impl : ( 'height' ) ;
    public final void rule__Scaler__Group__14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1488:1: ( ( 'height' ) )
            // InternalMediaFlow.g:1489:1: ( 'height' )
            {
            // InternalMediaFlow.g:1489:1: ( 'height' )
            // InternalMediaFlow.g:1490:2: 'height'
            {
             before(grammarAccess.getScalerAccess().getHeightKeyword_14()); 
            match(input,38,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getHeightKeyword_14()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__14__Impl"


    // $ANTLR start "rule__Scaler__Group__15"
    // InternalMediaFlow.g:1499:1: rule__Scaler__Group__15 : rule__Scaler__Group__15__Impl rule__Scaler__Group__16 ;
    public final void rule__Scaler__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1503:1: ( rule__Scaler__Group__15__Impl rule__Scaler__Group__16 )
            // InternalMediaFlow.g:1504:2: rule__Scaler__Group__15__Impl rule__Scaler__Group__16
            {
            pushFollow(FOLLOW_23);
            rule__Scaler__Group__15__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__16();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__15"


    // $ANTLR start "rule__Scaler__Group__15__Impl"
    // InternalMediaFlow.g:1511:1: rule__Scaler__Group__15__Impl : ( '=' ) ;
    public final void rule__Scaler__Group__15__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1515:1: ( ( '=' ) )
            // InternalMediaFlow.g:1516:1: ( '=' )
            {
            // InternalMediaFlow.g:1516:1: ( '=' )
            // InternalMediaFlow.g:1517:2: '='
            {
             before(grammarAccess.getScalerAccess().getEqualsSignKeyword_15()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getEqualsSignKeyword_15()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__15__Impl"


    // $ANTLR start "rule__Scaler__Group__16"
    // InternalMediaFlow.g:1526:1: rule__Scaler__Group__16 : rule__Scaler__Group__16__Impl rule__Scaler__Group__17 ;
    public final void rule__Scaler__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1530:1: ( rule__Scaler__Group__16__Impl rule__Scaler__Group__17 )
            // InternalMediaFlow.g:1531:2: rule__Scaler__Group__16__Impl rule__Scaler__Group__17
            {
            pushFollow(FOLLOW_4);
            rule__Scaler__Group__16__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__17();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__16"


    // $ANTLR start "rule__Scaler__Group__16__Impl"
    // InternalMediaFlow.g:1538:1: rule__Scaler__Group__16__Impl : ( ( rule__Scaler__HeightAssignment_16 ) ) ;
    public final void rule__Scaler__Group__16__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1542:1: ( ( ( rule__Scaler__HeightAssignment_16 ) ) )
            // InternalMediaFlow.g:1543:1: ( ( rule__Scaler__HeightAssignment_16 ) )
            {
            // InternalMediaFlow.g:1543:1: ( ( rule__Scaler__HeightAssignment_16 ) )
            // InternalMediaFlow.g:1544:2: ( rule__Scaler__HeightAssignment_16 )
            {
             before(grammarAccess.getScalerAccess().getHeightAssignment_16()); 
            // InternalMediaFlow.g:1545:2: ( rule__Scaler__HeightAssignment_16 )
            // InternalMediaFlow.g:1545:3: rule__Scaler__HeightAssignment_16
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__HeightAssignment_16();

            state._fsp--;


            }

             after(grammarAccess.getScalerAccess().getHeightAssignment_16()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__16__Impl"


    // $ANTLR start "rule__Scaler__Group__17"
    // InternalMediaFlow.g:1553:1: rule__Scaler__Group__17 : rule__Scaler__Group__17__Impl rule__Scaler__Group__18 ;
    public final void rule__Scaler__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1557:1: ( rule__Scaler__Group__17__Impl rule__Scaler__Group__18 )
            // InternalMediaFlow.g:1558:2: rule__Scaler__Group__17__Impl rule__Scaler__Group__18
            {
            pushFollow(FOLLOW_17);
            rule__Scaler__Group__17__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__18();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__17"


    // $ANTLR start "rule__Scaler__Group__17__Impl"
    // InternalMediaFlow.g:1565:1: rule__Scaler__Group__17__Impl : ( '{' ) ;
    public final void rule__Scaler__Group__17__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1569:1: ( ( '{' ) )
            // InternalMediaFlow.g:1570:1: ( '{' )
            {
            // InternalMediaFlow.g:1570:1: ( '{' )
            // InternalMediaFlow.g:1571:2: '{'
            {
             before(grammarAccess.getScalerAccess().getLeftCurlyBracketKeyword_17()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getLeftCurlyBracketKeyword_17()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__17__Impl"


    // $ANTLR start "rule__Scaler__Group__18"
    // InternalMediaFlow.g:1580:1: rule__Scaler__Group__18 : rule__Scaler__Group__18__Impl rule__Scaler__Group__19 ;
    public final void rule__Scaler__Group__18() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1584:1: ( rule__Scaler__Group__18__Impl rule__Scaler__Group__19 )
            // InternalMediaFlow.g:1585:2: rule__Scaler__Group__18__Impl rule__Scaler__Group__19
            {
            pushFollow(FOLLOW_17);
            rule__Scaler__Group__18__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Scaler__Group__19();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__18"


    // $ANTLR start "rule__Scaler__Group__18__Impl"
    // InternalMediaFlow.g:1592:1: rule__Scaler__Group__18__Impl : ( ( rule__Scaler__PortsAssignment_18 )* ) ;
    public final void rule__Scaler__Group__18__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1596:1: ( ( ( rule__Scaler__PortsAssignment_18 )* ) )
            // InternalMediaFlow.g:1597:1: ( ( rule__Scaler__PortsAssignment_18 )* )
            {
            // InternalMediaFlow.g:1597:1: ( ( rule__Scaler__PortsAssignment_18 )* )
            // InternalMediaFlow.g:1598:2: ( rule__Scaler__PortsAssignment_18 )*
            {
             before(grammarAccess.getScalerAccess().getPortsAssignment_18()); 
            // InternalMediaFlow.g:1599:2: ( rule__Scaler__PortsAssignment_18 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==44) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // InternalMediaFlow.g:1599:3: rule__Scaler__PortsAssignment_18
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__Scaler__PortsAssignment_18();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

             after(grammarAccess.getScalerAccess().getPortsAssignment_18()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__18__Impl"


    // $ANTLR start "rule__Scaler__Group__19"
    // InternalMediaFlow.g:1607:1: rule__Scaler__Group__19 : rule__Scaler__Group__19__Impl ;
    public final void rule__Scaler__Group__19() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1611:1: ( rule__Scaler__Group__19__Impl )
            // InternalMediaFlow.g:1612:2: rule__Scaler__Group__19__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Scaler__Group__19__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__19"


    // $ANTLR start "rule__Scaler__Group__19__Impl"
    // InternalMediaFlow.g:1618:1: rule__Scaler__Group__19__Impl : ( '}' ) ;
    public final void rule__Scaler__Group__19__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1622:1: ( ( '}' ) )
            // InternalMediaFlow.g:1623:1: ( '}' )
            {
            // InternalMediaFlow.g:1623:1: ( '}' )
            // InternalMediaFlow.g:1624:2: '}'
            {
             before(grammarAccess.getScalerAccess().getRightCurlyBracketKeyword_19()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getRightCurlyBracketKeyword_19()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__Group__19__Impl"


    // $ANTLR start "rule__Transcoder__Group__0"
    // InternalMediaFlow.g:1634:1: rule__Transcoder__Group__0 : rule__Transcoder__Group__0__Impl rule__Transcoder__Group__1 ;
    public final void rule__Transcoder__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1638:1: ( rule__Transcoder__Group__0__Impl rule__Transcoder__Group__1 )
            // InternalMediaFlow.g:1639:2: rule__Transcoder__Group__0__Impl rule__Transcoder__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__Transcoder__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__0"


    // $ANTLR start "rule__Transcoder__Group__0__Impl"
    // InternalMediaFlow.g:1646:1: rule__Transcoder__Group__0__Impl : ( 'transcoder' ) ;
    public final void rule__Transcoder__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1650:1: ( ( 'transcoder' ) )
            // InternalMediaFlow.g:1651:1: ( 'transcoder' )
            {
            // InternalMediaFlow.g:1651:1: ( 'transcoder' )
            // InternalMediaFlow.g:1652:2: 'transcoder'
            {
             before(grammarAccess.getTranscoderAccess().getTranscoderKeyword_0()); 
            match(input,39,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getTranscoderKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__0__Impl"


    // $ANTLR start "rule__Transcoder__Group__1"
    // InternalMediaFlow.g:1661:1: rule__Transcoder__Group__1 : rule__Transcoder__Group__1__Impl rule__Transcoder__Group__2 ;
    public final void rule__Transcoder__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1665:1: ( rule__Transcoder__Group__1__Impl rule__Transcoder__Group__2 )
            // InternalMediaFlow.g:1666:2: rule__Transcoder__Group__1__Impl rule__Transcoder__Group__2
            {
            pushFollow(FOLLOW_19);
            rule__Transcoder__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__1"


    // $ANTLR start "rule__Transcoder__Group__1__Impl"
    // InternalMediaFlow.g:1673:1: rule__Transcoder__Group__1__Impl : ( ( rule__Transcoder__NameAssignment_1 ) ) ;
    public final void rule__Transcoder__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1677:1: ( ( ( rule__Transcoder__NameAssignment_1 ) ) )
            // InternalMediaFlow.g:1678:1: ( ( rule__Transcoder__NameAssignment_1 ) )
            {
            // InternalMediaFlow.g:1678:1: ( ( rule__Transcoder__NameAssignment_1 ) )
            // InternalMediaFlow.g:1679:2: ( rule__Transcoder__NameAssignment_1 )
            {
             before(grammarAccess.getTranscoderAccess().getNameAssignment_1()); 
            // InternalMediaFlow.g:1680:2: ( rule__Transcoder__NameAssignment_1 )
            // InternalMediaFlow.g:1680:3: rule__Transcoder__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__1__Impl"


    // $ANTLR start "rule__Transcoder__Group__2"
    // InternalMediaFlow.g:1688:1: rule__Transcoder__Group__2 : rule__Transcoder__Group__2__Impl rule__Transcoder__Group__3 ;
    public final void rule__Transcoder__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1692:1: ( rule__Transcoder__Group__2__Impl rule__Transcoder__Group__3 )
            // InternalMediaFlow.g:1693:2: rule__Transcoder__Group__2__Impl rule__Transcoder__Group__3
            {
            pushFollow(FOLLOW_10);
            rule__Transcoder__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__2"


    // $ANTLR start "rule__Transcoder__Group__2__Impl"
    // InternalMediaFlow.g:1700:1: rule__Transcoder__Group__2__Impl : ( 'backend' ) ;
    public final void rule__Transcoder__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1704:1: ( ( 'backend' ) )
            // InternalMediaFlow.g:1705:1: ( 'backend' )
            {
            // InternalMediaFlow.g:1705:1: ( 'backend' )
            // InternalMediaFlow.g:1706:2: 'backend'
            {
             before(grammarAccess.getTranscoderAccess().getBackendKeyword_2()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getBackendKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__2__Impl"


    // $ANTLR start "rule__Transcoder__Group__3"
    // InternalMediaFlow.g:1715:1: rule__Transcoder__Group__3 : rule__Transcoder__Group__3__Impl rule__Transcoder__Group__4 ;
    public final void rule__Transcoder__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1719:1: ( rule__Transcoder__Group__3__Impl rule__Transcoder__Group__4 )
            // InternalMediaFlow.g:1720:2: rule__Transcoder__Group__3__Impl rule__Transcoder__Group__4
            {
            pushFollow(FOLLOW_20);
            rule__Transcoder__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__3"


    // $ANTLR start "rule__Transcoder__Group__3__Impl"
    // InternalMediaFlow.g:1727:1: rule__Transcoder__Group__3__Impl : ( '=' ) ;
    public final void rule__Transcoder__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1731:1: ( ( '=' ) )
            // InternalMediaFlow.g:1732:1: ( '=' )
            {
            // InternalMediaFlow.g:1732:1: ( '=' )
            // InternalMediaFlow.g:1733:2: '='
            {
             before(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_3()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__3__Impl"


    // $ANTLR start "rule__Transcoder__Group__4"
    // InternalMediaFlow.g:1742:1: rule__Transcoder__Group__4 : rule__Transcoder__Group__4__Impl rule__Transcoder__Group__5 ;
    public final void rule__Transcoder__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1746:1: ( rule__Transcoder__Group__4__Impl rule__Transcoder__Group__5 )
            // InternalMediaFlow.g:1747:2: rule__Transcoder__Group__4__Impl rule__Transcoder__Group__5
            {
            pushFollow(FOLLOW_21);
            rule__Transcoder__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__4"


    // $ANTLR start "rule__Transcoder__Group__4__Impl"
    // InternalMediaFlow.g:1754:1: rule__Transcoder__Group__4__Impl : ( ( rule__Transcoder__BackendAssignment_4 ) ) ;
    public final void rule__Transcoder__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1758:1: ( ( ( rule__Transcoder__BackendAssignment_4 ) ) )
            // InternalMediaFlow.g:1759:1: ( ( rule__Transcoder__BackendAssignment_4 ) )
            {
            // InternalMediaFlow.g:1759:1: ( ( rule__Transcoder__BackendAssignment_4 ) )
            // InternalMediaFlow.g:1760:2: ( rule__Transcoder__BackendAssignment_4 )
            {
             before(grammarAccess.getTranscoderAccess().getBackendAssignment_4()); 
            // InternalMediaFlow.g:1761:2: ( rule__Transcoder__BackendAssignment_4 )
            // InternalMediaFlow.g:1761:3: rule__Transcoder__BackendAssignment_4
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__BackendAssignment_4();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getBackendAssignment_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__4__Impl"


    // $ANTLR start "rule__Transcoder__Group__5"
    // InternalMediaFlow.g:1769:1: rule__Transcoder__Group__5 : rule__Transcoder__Group__5__Impl rule__Transcoder__Group__6 ;
    public final void rule__Transcoder__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1773:1: ( rule__Transcoder__Group__5__Impl rule__Transcoder__Group__6 )
            // InternalMediaFlow.g:1774:2: rule__Transcoder__Group__5__Impl rule__Transcoder__Group__6
            {
            pushFollow(FOLLOW_10);
            rule__Transcoder__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__5"


    // $ANTLR start "rule__Transcoder__Group__5__Impl"
    // InternalMediaFlow.g:1781:1: rule__Transcoder__Group__5__Impl : ( 'command' ) ;
    public final void rule__Transcoder__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1785:1: ( ( 'command' ) )
            // InternalMediaFlow.g:1786:1: ( 'command' )
            {
            // InternalMediaFlow.g:1786:1: ( 'command' )
            // InternalMediaFlow.g:1787:2: 'command'
            {
             before(grammarAccess.getTranscoderAccess().getCommandKeyword_5()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getCommandKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__5__Impl"


    // $ANTLR start "rule__Transcoder__Group__6"
    // InternalMediaFlow.g:1796:1: rule__Transcoder__Group__6 : rule__Transcoder__Group__6__Impl rule__Transcoder__Group__7 ;
    public final void rule__Transcoder__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1800:1: ( rule__Transcoder__Group__6__Impl rule__Transcoder__Group__7 )
            // InternalMediaFlow.g:1801:2: rule__Transcoder__Group__6__Impl rule__Transcoder__Group__7
            {
            pushFollow(FOLLOW_14);
            rule__Transcoder__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__6"


    // $ANTLR start "rule__Transcoder__Group__6__Impl"
    // InternalMediaFlow.g:1808:1: rule__Transcoder__Group__6__Impl : ( '=' ) ;
    public final void rule__Transcoder__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1812:1: ( ( '=' ) )
            // InternalMediaFlow.g:1813:1: ( '=' )
            {
            // InternalMediaFlow.g:1813:1: ( '=' )
            // InternalMediaFlow.g:1814:2: '='
            {
             before(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_6()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__6__Impl"


    // $ANTLR start "rule__Transcoder__Group__7"
    // InternalMediaFlow.g:1823:1: rule__Transcoder__Group__7 : rule__Transcoder__Group__7__Impl rule__Transcoder__Group__8 ;
    public final void rule__Transcoder__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1827:1: ( rule__Transcoder__Group__7__Impl rule__Transcoder__Group__8 )
            // InternalMediaFlow.g:1828:2: rule__Transcoder__Group__7__Impl rule__Transcoder__Group__8
            {
            pushFollow(FOLLOW_22);
            rule__Transcoder__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__7"


    // $ANTLR start "rule__Transcoder__Group__7__Impl"
    // InternalMediaFlow.g:1835:1: rule__Transcoder__Group__7__Impl : ( ( rule__Transcoder__CommandAssignment_7 ) ) ;
    public final void rule__Transcoder__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1839:1: ( ( ( rule__Transcoder__CommandAssignment_7 ) ) )
            // InternalMediaFlow.g:1840:1: ( ( rule__Transcoder__CommandAssignment_7 ) )
            {
            // InternalMediaFlow.g:1840:1: ( ( rule__Transcoder__CommandAssignment_7 ) )
            // InternalMediaFlow.g:1841:2: ( rule__Transcoder__CommandAssignment_7 )
            {
             before(grammarAccess.getTranscoderAccess().getCommandAssignment_7()); 
            // InternalMediaFlow.g:1842:2: ( rule__Transcoder__CommandAssignment_7 )
            // InternalMediaFlow.g:1842:3: rule__Transcoder__CommandAssignment_7
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__CommandAssignment_7();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getCommandAssignment_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__7__Impl"


    // $ANTLR start "rule__Transcoder__Group__8"
    // InternalMediaFlow.g:1850:1: rule__Transcoder__Group__8 : rule__Transcoder__Group__8__Impl rule__Transcoder__Group__9 ;
    public final void rule__Transcoder__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1854:1: ( rule__Transcoder__Group__8__Impl rule__Transcoder__Group__9 )
            // InternalMediaFlow.g:1855:2: rule__Transcoder__Group__8__Impl rule__Transcoder__Group__9
            {
            pushFollow(FOLLOW_10);
            rule__Transcoder__Group__8__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__9();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__8"


    // $ANTLR start "rule__Transcoder__Group__8__Impl"
    // InternalMediaFlow.g:1862:1: rule__Transcoder__Group__8__Impl : ( 'replicas' ) ;
    public final void rule__Transcoder__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1866:1: ( ( 'replicas' ) )
            // InternalMediaFlow.g:1867:1: ( 'replicas' )
            {
            // InternalMediaFlow.g:1867:1: ( 'replicas' )
            // InternalMediaFlow.g:1868:2: 'replicas'
            {
             before(grammarAccess.getTranscoderAccess().getReplicasKeyword_8()); 
            match(input,36,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getReplicasKeyword_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__8__Impl"


    // $ANTLR start "rule__Transcoder__Group__9"
    // InternalMediaFlow.g:1877:1: rule__Transcoder__Group__9 : rule__Transcoder__Group__9__Impl rule__Transcoder__Group__10 ;
    public final void rule__Transcoder__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1881:1: ( rule__Transcoder__Group__9__Impl rule__Transcoder__Group__10 )
            // InternalMediaFlow.g:1882:2: rule__Transcoder__Group__9__Impl rule__Transcoder__Group__10
            {
            pushFollow(FOLLOW_23);
            rule__Transcoder__Group__9__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__10();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__9"


    // $ANTLR start "rule__Transcoder__Group__9__Impl"
    // InternalMediaFlow.g:1889:1: rule__Transcoder__Group__9__Impl : ( '=' ) ;
    public final void rule__Transcoder__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1893:1: ( ( '=' ) )
            // InternalMediaFlow.g:1894:1: ( '=' )
            {
            // InternalMediaFlow.g:1894:1: ( '=' )
            // InternalMediaFlow.g:1895:2: '='
            {
             before(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_9()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_9()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__9__Impl"


    // $ANTLR start "rule__Transcoder__Group__10"
    // InternalMediaFlow.g:1904:1: rule__Transcoder__Group__10 : rule__Transcoder__Group__10__Impl rule__Transcoder__Group__11 ;
    public final void rule__Transcoder__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1908:1: ( rule__Transcoder__Group__10__Impl rule__Transcoder__Group__11 )
            // InternalMediaFlow.g:1909:2: rule__Transcoder__Group__10__Impl rule__Transcoder__Group__11
            {
            pushFollow(FOLLOW_26);
            rule__Transcoder__Group__10__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__11();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__10"


    // $ANTLR start "rule__Transcoder__Group__10__Impl"
    // InternalMediaFlow.g:1916:1: rule__Transcoder__Group__10__Impl : ( ( rule__Transcoder__ReplicasAssignment_10 ) ) ;
    public final void rule__Transcoder__Group__10__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1920:1: ( ( ( rule__Transcoder__ReplicasAssignment_10 ) ) )
            // InternalMediaFlow.g:1921:1: ( ( rule__Transcoder__ReplicasAssignment_10 ) )
            {
            // InternalMediaFlow.g:1921:1: ( ( rule__Transcoder__ReplicasAssignment_10 ) )
            // InternalMediaFlow.g:1922:2: ( rule__Transcoder__ReplicasAssignment_10 )
            {
             before(grammarAccess.getTranscoderAccess().getReplicasAssignment_10()); 
            // InternalMediaFlow.g:1923:2: ( rule__Transcoder__ReplicasAssignment_10 )
            // InternalMediaFlow.g:1923:3: rule__Transcoder__ReplicasAssignment_10
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__ReplicasAssignment_10();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getReplicasAssignment_10()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__10__Impl"


    // $ANTLR start "rule__Transcoder__Group__11"
    // InternalMediaFlow.g:1931:1: rule__Transcoder__Group__11 : rule__Transcoder__Group__11__Impl rule__Transcoder__Group__12 ;
    public final void rule__Transcoder__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1935:1: ( rule__Transcoder__Group__11__Impl rule__Transcoder__Group__12 )
            // InternalMediaFlow.g:1936:2: rule__Transcoder__Group__11__Impl rule__Transcoder__Group__12
            {
            pushFollow(FOLLOW_10);
            rule__Transcoder__Group__11__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__12();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__11"


    // $ANTLR start "rule__Transcoder__Group__11__Impl"
    // InternalMediaFlow.g:1943:1: rule__Transcoder__Group__11__Impl : ( 'videoCodec' ) ;
    public final void rule__Transcoder__Group__11__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1947:1: ( ( 'videoCodec' ) )
            // InternalMediaFlow.g:1948:1: ( 'videoCodec' )
            {
            // InternalMediaFlow.g:1948:1: ( 'videoCodec' )
            // InternalMediaFlow.g:1949:2: 'videoCodec'
            {
             before(grammarAccess.getTranscoderAccess().getVideoCodecKeyword_11()); 
            match(input,40,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getVideoCodecKeyword_11()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__11__Impl"


    // $ANTLR start "rule__Transcoder__Group__12"
    // InternalMediaFlow.g:1958:1: rule__Transcoder__Group__12 : rule__Transcoder__Group__12__Impl rule__Transcoder__Group__13 ;
    public final void rule__Transcoder__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1962:1: ( rule__Transcoder__Group__12__Impl rule__Transcoder__Group__13 )
            // InternalMediaFlow.g:1963:2: rule__Transcoder__Group__12__Impl rule__Transcoder__Group__13
            {
            pushFollow(FOLLOW_14);
            rule__Transcoder__Group__12__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__13();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__12"


    // $ANTLR start "rule__Transcoder__Group__12__Impl"
    // InternalMediaFlow.g:1970:1: rule__Transcoder__Group__12__Impl : ( '=' ) ;
    public final void rule__Transcoder__Group__12__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1974:1: ( ( '=' ) )
            // InternalMediaFlow.g:1975:1: ( '=' )
            {
            // InternalMediaFlow.g:1975:1: ( '=' )
            // InternalMediaFlow.g:1976:2: '='
            {
             before(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_12()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_12()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__12__Impl"


    // $ANTLR start "rule__Transcoder__Group__13"
    // InternalMediaFlow.g:1985:1: rule__Transcoder__Group__13 : rule__Transcoder__Group__13__Impl rule__Transcoder__Group__14 ;
    public final void rule__Transcoder__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:1989:1: ( rule__Transcoder__Group__13__Impl rule__Transcoder__Group__14 )
            // InternalMediaFlow.g:1990:2: rule__Transcoder__Group__13__Impl rule__Transcoder__Group__14
            {
            pushFollow(FOLLOW_27);
            rule__Transcoder__Group__13__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__14();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__13"


    // $ANTLR start "rule__Transcoder__Group__13__Impl"
    // InternalMediaFlow.g:1997:1: rule__Transcoder__Group__13__Impl : ( ( rule__Transcoder__VideoCodecAssignment_13 ) ) ;
    public final void rule__Transcoder__Group__13__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2001:1: ( ( ( rule__Transcoder__VideoCodecAssignment_13 ) ) )
            // InternalMediaFlow.g:2002:1: ( ( rule__Transcoder__VideoCodecAssignment_13 ) )
            {
            // InternalMediaFlow.g:2002:1: ( ( rule__Transcoder__VideoCodecAssignment_13 ) )
            // InternalMediaFlow.g:2003:2: ( rule__Transcoder__VideoCodecAssignment_13 )
            {
             before(grammarAccess.getTranscoderAccess().getVideoCodecAssignment_13()); 
            // InternalMediaFlow.g:2004:2: ( rule__Transcoder__VideoCodecAssignment_13 )
            // InternalMediaFlow.g:2004:3: rule__Transcoder__VideoCodecAssignment_13
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__VideoCodecAssignment_13();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getVideoCodecAssignment_13()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__13__Impl"


    // $ANTLR start "rule__Transcoder__Group__14"
    // InternalMediaFlow.g:2012:1: rule__Transcoder__Group__14 : rule__Transcoder__Group__14__Impl rule__Transcoder__Group__15 ;
    public final void rule__Transcoder__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2016:1: ( rule__Transcoder__Group__14__Impl rule__Transcoder__Group__15 )
            // InternalMediaFlow.g:2017:2: rule__Transcoder__Group__14__Impl rule__Transcoder__Group__15
            {
            pushFollow(FOLLOW_10);
            rule__Transcoder__Group__14__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__15();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__14"


    // $ANTLR start "rule__Transcoder__Group__14__Impl"
    // InternalMediaFlow.g:2024:1: rule__Transcoder__Group__14__Impl : ( 'audioCodec' ) ;
    public final void rule__Transcoder__Group__14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2028:1: ( ( 'audioCodec' ) )
            // InternalMediaFlow.g:2029:1: ( 'audioCodec' )
            {
            // InternalMediaFlow.g:2029:1: ( 'audioCodec' )
            // InternalMediaFlow.g:2030:2: 'audioCodec'
            {
             before(grammarAccess.getTranscoderAccess().getAudioCodecKeyword_14()); 
            match(input,41,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getAudioCodecKeyword_14()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__14__Impl"


    // $ANTLR start "rule__Transcoder__Group__15"
    // InternalMediaFlow.g:2039:1: rule__Transcoder__Group__15 : rule__Transcoder__Group__15__Impl rule__Transcoder__Group__16 ;
    public final void rule__Transcoder__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2043:1: ( rule__Transcoder__Group__15__Impl rule__Transcoder__Group__16 )
            // InternalMediaFlow.g:2044:2: rule__Transcoder__Group__15__Impl rule__Transcoder__Group__16
            {
            pushFollow(FOLLOW_14);
            rule__Transcoder__Group__15__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__16();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__15"


    // $ANTLR start "rule__Transcoder__Group__15__Impl"
    // InternalMediaFlow.g:2051:1: rule__Transcoder__Group__15__Impl : ( '=' ) ;
    public final void rule__Transcoder__Group__15__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2055:1: ( ( '=' ) )
            // InternalMediaFlow.g:2056:1: ( '=' )
            {
            // InternalMediaFlow.g:2056:1: ( '=' )
            // InternalMediaFlow.g:2057:2: '='
            {
             before(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_15()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_15()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__15__Impl"


    // $ANTLR start "rule__Transcoder__Group__16"
    // InternalMediaFlow.g:2066:1: rule__Transcoder__Group__16 : rule__Transcoder__Group__16__Impl rule__Transcoder__Group__17 ;
    public final void rule__Transcoder__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2070:1: ( rule__Transcoder__Group__16__Impl rule__Transcoder__Group__17 )
            // InternalMediaFlow.g:2071:2: rule__Transcoder__Group__16__Impl rule__Transcoder__Group__17
            {
            pushFollow(FOLLOW_28);
            rule__Transcoder__Group__16__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__17();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__16"


    // $ANTLR start "rule__Transcoder__Group__16__Impl"
    // InternalMediaFlow.g:2078:1: rule__Transcoder__Group__16__Impl : ( ( rule__Transcoder__AudioCodecAssignment_16 ) ) ;
    public final void rule__Transcoder__Group__16__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2082:1: ( ( ( rule__Transcoder__AudioCodecAssignment_16 ) ) )
            // InternalMediaFlow.g:2083:1: ( ( rule__Transcoder__AudioCodecAssignment_16 ) )
            {
            // InternalMediaFlow.g:2083:1: ( ( rule__Transcoder__AudioCodecAssignment_16 ) )
            // InternalMediaFlow.g:2084:2: ( rule__Transcoder__AudioCodecAssignment_16 )
            {
             before(grammarAccess.getTranscoderAccess().getAudioCodecAssignment_16()); 
            // InternalMediaFlow.g:2085:2: ( rule__Transcoder__AudioCodecAssignment_16 )
            // InternalMediaFlow.g:2085:3: rule__Transcoder__AudioCodecAssignment_16
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__AudioCodecAssignment_16();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getAudioCodecAssignment_16()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__16__Impl"


    // $ANTLR start "rule__Transcoder__Group__17"
    // InternalMediaFlow.g:2093:1: rule__Transcoder__Group__17 : rule__Transcoder__Group__17__Impl rule__Transcoder__Group__18 ;
    public final void rule__Transcoder__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2097:1: ( rule__Transcoder__Group__17__Impl rule__Transcoder__Group__18 )
            // InternalMediaFlow.g:2098:2: rule__Transcoder__Group__17__Impl rule__Transcoder__Group__18
            {
            pushFollow(FOLLOW_10);
            rule__Transcoder__Group__17__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__18();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__17"


    // $ANTLR start "rule__Transcoder__Group__17__Impl"
    // InternalMediaFlow.g:2105:1: rule__Transcoder__Group__17__Impl : ( 'container' ) ;
    public final void rule__Transcoder__Group__17__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2109:1: ( ( 'container' ) )
            // InternalMediaFlow.g:2110:1: ( 'container' )
            {
            // InternalMediaFlow.g:2110:1: ( 'container' )
            // InternalMediaFlow.g:2111:2: 'container'
            {
             before(grammarAccess.getTranscoderAccess().getContainerKeyword_17()); 
            match(input,42,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getContainerKeyword_17()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__17__Impl"


    // $ANTLR start "rule__Transcoder__Group__18"
    // InternalMediaFlow.g:2120:1: rule__Transcoder__Group__18 : rule__Transcoder__Group__18__Impl rule__Transcoder__Group__19 ;
    public final void rule__Transcoder__Group__18() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2124:1: ( rule__Transcoder__Group__18__Impl rule__Transcoder__Group__19 )
            // InternalMediaFlow.g:2125:2: rule__Transcoder__Group__18__Impl rule__Transcoder__Group__19
            {
            pushFollow(FOLLOW_14);
            rule__Transcoder__Group__18__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__19();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__18"


    // $ANTLR start "rule__Transcoder__Group__18__Impl"
    // InternalMediaFlow.g:2132:1: rule__Transcoder__Group__18__Impl : ( '=' ) ;
    public final void rule__Transcoder__Group__18__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2136:1: ( ( '=' ) )
            // InternalMediaFlow.g:2137:1: ( '=' )
            {
            // InternalMediaFlow.g:2137:1: ( '=' )
            // InternalMediaFlow.g:2138:2: '='
            {
             before(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_18()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_18()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__18__Impl"


    // $ANTLR start "rule__Transcoder__Group__19"
    // InternalMediaFlow.g:2147:1: rule__Transcoder__Group__19 : rule__Transcoder__Group__19__Impl rule__Transcoder__Group__20 ;
    public final void rule__Transcoder__Group__19() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2151:1: ( rule__Transcoder__Group__19__Impl rule__Transcoder__Group__20 )
            // InternalMediaFlow.g:2152:2: rule__Transcoder__Group__19__Impl rule__Transcoder__Group__20
            {
            pushFollow(FOLLOW_29);
            rule__Transcoder__Group__19__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__20();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__19"


    // $ANTLR start "rule__Transcoder__Group__19__Impl"
    // InternalMediaFlow.g:2159:1: rule__Transcoder__Group__19__Impl : ( ( rule__Transcoder__ContainerAssignment_19 ) ) ;
    public final void rule__Transcoder__Group__19__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2163:1: ( ( ( rule__Transcoder__ContainerAssignment_19 ) ) )
            // InternalMediaFlow.g:2164:1: ( ( rule__Transcoder__ContainerAssignment_19 ) )
            {
            // InternalMediaFlow.g:2164:1: ( ( rule__Transcoder__ContainerAssignment_19 ) )
            // InternalMediaFlow.g:2165:2: ( rule__Transcoder__ContainerAssignment_19 )
            {
             before(grammarAccess.getTranscoderAccess().getContainerAssignment_19()); 
            // InternalMediaFlow.g:2166:2: ( rule__Transcoder__ContainerAssignment_19 )
            // InternalMediaFlow.g:2166:3: rule__Transcoder__ContainerAssignment_19
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__ContainerAssignment_19();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getContainerAssignment_19()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__19__Impl"


    // $ANTLR start "rule__Transcoder__Group__20"
    // InternalMediaFlow.g:2174:1: rule__Transcoder__Group__20 : rule__Transcoder__Group__20__Impl rule__Transcoder__Group__21 ;
    public final void rule__Transcoder__Group__20() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2178:1: ( rule__Transcoder__Group__20__Impl rule__Transcoder__Group__21 )
            // InternalMediaFlow.g:2179:2: rule__Transcoder__Group__20__Impl rule__Transcoder__Group__21
            {
            pushFollow(FOLLOW_10);
            rule__Transcoder__Group__20__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__21();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__20"


    // $ANTLR start "rule__Transcoder__Group__20__Impl"
    // InternalMediaFlow.g:2186:1: rule__Transcoder__Group__20__Impl : ( 'bitrate' ) ;
    public final void rule__Transcoder__Group__20__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2190:1: ( ( 'bitrate' ) )
            // InternalMediaFlow.g:2191:1: ( 'bitrate' )
            {
            // InternalMediaFlow.g:2191:1: ( 'bitrate' )
            // InternalMediaFlow.g:2192:2: 'bitrate'
            {
             before(grammarAccess.getTranscoderAccess().getBitrateKeyword_20()); 
            match(input,43,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getBitrateKeyword_20()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__20__Impl"


    // $ANTLR start "rule__Transcoder__Group__21"
    // InternalMediaFlow.g:2201:1: rule__Transcoder__Group__21 : rule__Transcoder__Group__21__Impl rule__Transcoder__Group__22 ;
    public final void rule__Transcoder__Group__21() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2205:1: ( rule__Transcoder__Group__21__Impl rule__Transcoder__Group__22 )
            // InternalMediaFlow.g:2206:2: rule__Transcoder__Group__21__Impl rule__Transcoder__Group__22
            {
            pushFollow(FOLLOW_23);
            rule__Transcoder__Group__21__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__22();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__21"


    // $ANTLR start "rule__Transcoder__Group__21__Impl"
    // InternalMediaFlow.g:2213:1: rule__Transcoder__Group__21__Impl : ( '=' ) ;
    public final void rule__Transcoder__Group__21__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2217:1: ( ( '=' ) )
            // InternalMediaFlow.g:2218:1: ( '=' )
            {
            // InternalMediaFlow.g:2218:1: ( '=' )
            // InternalMediaFlow.g:2219:2: '='
            {
             before(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_21()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getEqualsSignKeyword_21()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__21__Impl"


    // $ANTLR start "rule__Transcoder__Group__22"
    // InternalMediaFlow.g:2228:1: rule__Transcoder__Group__22 : rule__Transcoder__Group__22__Impl rule__Transcoder__Group__23 ;
    public final void rule__Transcoder__Group__22() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2232:1: ( rule__Transcoder__Group__22__Impl rule__Transcoder__Group__23 )
            // InternalMediaFlow.g:2233:2: rule__Transcoder__Group__22__Impl rule__Transcoder__Group__23
            {
            pushFollow(FOLLOW_4);
            rule__Transcoder__Group__22__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__23();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__22"


    // $ANTLR start "rule__Transcoder__Group__22__Impl"
    // InternalMediaFlow.g:2240:1: rule__Transcoder__Group__22__Impl : ( ( rule__Transcoder__BitrateAssignment_22 ) ) ;
    public final void rule__Transcoder__Group__22__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2244:1: ( ( ( rule__Transcoder__BitrateAssignment_22 ) ) )
            // InternalMediaFlow.g:2245:1: ( ( rule__Transcoder__BitrateAssignment_22 ) )
            {
            // InternalMediaFlow.g:2245:1: ( ( rule__Transcoder__BitrateAssignment_22 ) )
            // InternalMediaFlow.g:2246:2: ( rule__Transcoder__BitrateAssignment_22 )
            {
             before(grammarAccess.getTranscoderAccess().getBitrateAssignment_22()); 
            // InternalMediaFlow.g:2247:2: ( rule__Transcoder__BitrateAssignment_22 )
            // InternalMediaFlow.g:2247:3: rule__Transcoder__BitrateAssignment_22
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__BitrateAssignment_22();

            state._fsp--;


            }

             after(grammarAccess.getTranscoderAccess().getBitrateAssignment_22()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__22__Impl"


    // $ANTLR start "rule__Transcoder__Group__23"
    // InternalMediaFlow.g:2255:1: rule__Transcoder__Group__23 : rule__Transcoder__Group__23__Impl rule__Transcoder__Group__24 ;
    public final void rule__Transcoder__Group__23() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2259:1: ( rule__Transcoder__Group__23__Impl rule__Transcoder__Group__24 )
            // InternalMediaFlow.g:2260:2: rule__Transcoder__Group__23__Impl rule__Transcoder__Group__24
            {
            pushFollow(FOLLOW_17);
            rule__Transcoder__Group__23__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__24();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__23"


    // $ANTLR start "rule__Transcoder__Group__23__Impl"
    // InternalMediaFlow.g:2267:1: rule__Transcoder__Group__23__Impl : ( '{' ) ;
    public final void rule__Transcoder__Group__23__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2271:1: ( ( '{' ) )
            // InternalMediaFlow.g:2272:1: ( '{' )
            {
            // InternalMediaFlow.g:2272:1: ( '{' )
            // InternalMediaFlow.g:2273:2: '{'
            {
             before(grammarAccess.getTranscoderAccess().getLeftCurlyBracketKeyword_23()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getLeftCurlyBracketKeyword_23()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__23__Impl"


    // $ANTLR start "rule__Transcoder__Group__24"
    // InternalMediaFlow.g:2282:1: rule__Transcoder__Group__24 : rule__Transcoder__Group__24__Impl rule__Transcoder__Group__25 ;
    public final void rule__Transcoder__Group__24() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2286:1: ( rule__Transcoder__Group__24__Impl rule__Transcoder__Group__25 )
            // InternalMediaFlow.g:2287:2: rule__Transcoder__Group__24__Impl rule__Transcoder__Group__25
            {
            pushFollow(FOLLOW_17);
            rule__Transcoder__Group__24__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__25();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__24"


    // $ANTLR start "rule__Transcoder__Group__24__Impl"
    // InternalMediaFlow.g:2294:1: rule__Transcoder__Group__24__Impl : ( ( rule__Transcoder__PortsAssignment_24 )* ) ;
    public final void rule__Transcoder__Group__24__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2298:1: ( ( ( rule__Transcoder__PortsAssignment_24 )* ) )
            // InternalMediaFlow.g:2299:1: ( ( rule__Transcoder__PortsAssignment_24 )* )
            {
            // InternalMediaFlow.g:2299:1: ( ( rule__Transcoder__PortsAssignment_24 )* )
            // InternalMediaFlow.g:2300:2: ( rule__Transcoder__PortsAssignment_24 )*
            {
             before(grammarAccess.getTranscoderAccess().getPortsAssignment_24()); 
            // InternalMediaFlow.g:2301:2: ( rule__Transcoder__PortsAssignment_24 )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==44) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // InternalMediaFlow.g:2301:3: rule__Transcoder__PortsAssignment_24
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__Transcoder__PortsAssignment_24();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

             after(grammarAccess.getTranscoderAccess().getPortsAssignment_24()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__24__Impl"


    // $ANTLR start "rule__Transcoder__Group__25"
    // InternalMediaFlow.g:2309:1: rule__Transcoder__Group__25 : rule__Transcoder__Group__25__Impl ;
    public final void rule__Transcoder__Group__25() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2313:1: ( rule__Transcoder__Group__25__Impl )
            // InternalMediaFlow.g:2314:2: rule__Transcoder__Group__25__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Transcoder__Group__25__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__25"


    // $ANTLR start "rule__Transcoder__Group__25__Impl"
    // InternalMediaFlow.g:2320:1: rule__Transcoder__Group__25__Impl : ( '}' ) ;
    public final void rule__Transcoder__Group__25__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2324:1: ( ( '}' ) )
            // InternalMediaFlow.g:2325:1: ( '}' )
            {
            // InternalMediaFlow.g:2325:1: ( '}' )
            // InternalMediaFlow.g:2326:2: '}'
            {
             before(grammarAccess.getTranscoderAccess().getRightCurlyBracketKeyword_25()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getRightCurlyBracketKeyword_25()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__Group__25__Impl"


    // $ANTLR start "rule__Port__Group__0"
    // InternalMediaFlow.g:2336:1: rule__Port__Group__0 : rule__Port__Group__0__Impl rule__Port__Group__1 ;
    public final void rule__Port__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2340:1: ( rule__Port__Group__0__Impl rule__Port__Group__1 )
            // InternalMediaFlow.g:2341:2: rule__Port__Group__0__Impl rule__Port__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__Port__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Port__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__Group__0"


    // $ANTLR start "rule__Port__Group__0__Impl"
    // InternalMediaFlow.g:2348:1: rule__Port__Group__0__Impl : ( 'port' ) ;
    public final void rule__Port__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2352:1: ( ( 'port' ) )
            // InternalMediaFlow.g:2353:1: ( 'port' )
            {
            // InternalMediaFlow.g:2353:1: ( 'port' )
            // InternalMediaFlow.g:2354:2: 'port'
            {
             before(grammarAccess.getPortAccess().getPortKeyword_0()); 
            match(input,44,FOLLOW_2); 
             after(grammarAccess.getPortAccess().getPortKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__Group__0__Impl"


    // $ANTLR start "rule__Port__Group__1"
    // InternalMediaFlow.g:2363:1: rule__Port__Group__1 : rule__Port__Group__1__Impl rule__Port__Group__2 ;
    public final void rule__Port__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2367:1: ( rule__Port__Group__1__Impl rule__Port__Group__2 )
            // InternalMediaFlow.g:2368:2: rule__Port__Group__1__Impl rule__Port__Group__2
            {
            pushFollow(FOLLOW_30);
            rule__Port__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Port__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__Group__1"


    // $ANTLR start "rule__Port__Group__1__Impl"
    // InternalMediaFlow.g:2375:1: rule__Port__Group__1__Impl : ( ( rule__Port__NameAssignment_1 ) ) ;
    public final void rule__Port__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2379:1: ( ( ( rule__Port__NameAssignment_1 ) ) )
            // InternalMediaFlow.g:2380:1: ( ( rule__Port__NameAssignment_1 ) )
            {
            // InternalMediaFlow.g:2380:1: ( ( rule__Port__NameAssignment_1 ) )
            // InternalMediaFlow.g:2381:2: ( rule__Port__NameAssignment_1 )
            {
             before(grammarAccess.getPortAccess().getNameAssignment_1()); 
            // InternalMediaFlow.g:2382:2: ( rule__Port__NameAssignment_1 )
            // InternalMediaFlow.g:2382:3: rule__Port__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Port__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getPortAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__Group__1__Impl"


    // $ANTLR start "rule__Port__Group__2"
    // InternalMediaFlow.g:2390:1: rule__Port__Group__2 : rule__Port__Group__2__Impl ;
    public final void rule__Port__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2394:1: ( rule__Port__Group__2__Impl )
            // InternalMediaFlow.g:2395:2: rule__Port__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Port__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__Group__2"


    // $ANTLR start "rule__Port__Group__2__Impl"
    // InternalMediaFlow.g:2401:1: rule__Port__Group__2__Impl : ( ( rule__Port__DirectionAssignment_2 ) ) ;
    public final void rule__Port__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2405:1: ( ( ( rule__Port__DirectionAssignment_2 ) ) )
            // InternalMediaFlow.g:2406:1: ( ( rule__Port__DirectionAssignment_2 ) )
            {
            // InternalMediaFlow.g:2406:1: ( ( rule__Port__DirectionAssignment_2 ) )
            // InternalMediaFlow.g:2407:2: ( rule__Port__DirectionAssignment_2 )
            {
             before(grammarAccess.getPortAccess().getDirectionAssignment_2()); 
            // InternalMediaFlow.g:2408:2: ( rule__Port__DirectionAssignment_2 )
            // InternalMediaFlow.g:2408:3: rule__Port__DirectionAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__Port__DirectionAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getPortAccess().getDirectionAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__Group__2__Impl"


    // $ANTLR start "rule__Edge__Group__0"
    // InternalMediaFlow.g:2417:1: rule__Edge__Group__0 : rule__Edge__Group__0__Impl rule__Edge__Group__1 ;
    public final void rule__Edge__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2421:1: ( rule__Edge__Group__0__Impl rule__Edge__Group__1 )
            // InternalMediaFlow.g:2422:2: rule__Edge__Group__0__Impl rule__Edge__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__Edge__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Edge__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__0"


    // $ANTLR start "rule__Edge__Group__0__Impl"
    // InternalMediaFlow.g:2429:1: rule__Edge__Group__0__Impl : ( 'edge' ) ;
    public final void rule__Edge__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2433:1: ( ( 'edge' ) )
            // InternalMediaFlow.g:2434:1: ( 'edge' )
            {
            // InternalMediaFlow.g:2434:1: ( 'edge' )
            // InternalMediaFlow.g:2435:2: 'edge'
            {
             before(grammarAccess.getEdgeAccess().getEdgeKeyword_0()); 
            match(input,45,FOLLOW_2); 
             after(grammarAccess.getEdgeAccess().getEdgeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__0__Impl"


    // $ANTLR start "rule__Edge__Group__1"
    // InternalMediaFlow.g:2444:1: rule__Edge__Group__1 : rule__Edge__Group__1__Impl rule__Edge__Group__2 ;
    public final void rule__Edge__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2448:1: ( rule__Edge__Group__1__Impl rule__Edge__Group__2 )
            // InternalMediaFlow.g:2449:2: rule__Edge__Group__1__Impl rule__Edge__Group__2
            {
            pushFollow(FOLLOW_31);
            rule__Edge__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Edge__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__1"


    // $ANTLR start "rule__Edge__Group__1__Impl"
    // InternalMediaFlow.g:2456:1: rule__Edge__Group__1__Impl : ( ( rule__Edge__NameAssignment_1 ) ) ;
    public final void rule__Edge__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2460:1: ( ( ( rule__Edge__NameAssignment_1 ) ) )
            // InternalMediaFlow.g:2461:1: ( ( rule__Edge__NameAssignment_1 ) )
            {
            // InternalMediaFlow.g:2461:1: ( ( rule__Edge__NameAssignment_1 ) )
            // InternalMediaFlow.g:2462:2: ( rule__Edge__NameAssignment_1 )
            {
             before(grammarAccess.getEdgeAccess().getNameAssignment_1()); 
            // InternalMediaFlow.g:2463:2: ( rule__Edge__NameAssignment_1 )
            // InternalMediaFlow.g:2463:3: rule__Edge__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Edge__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getEdgeAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__1__Impl"


    // $ANTLR start "rule__Edge__Group__2"
    // InternalMediaFlow.g:2471:1: rule__Edge__Group__2 : rule__Edge__Group__2__Impl rule__Edge__Group__3 ;
    public final void rule__Edge__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2475:1: ( rule__Edge__Group__2__Impl rule__Edge__Group__3 )
            // InternalMediaFlow.g:2476:2: rule__Edge__Group__2__Impl rule__Edge__Group__3
            {
            pushFollow(FOLLOW_3);
            rule__Edge__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Edge__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__2"


    // $ANTLR start "rule__Edge__Group__2__Impl"
    // InternalMediaFlow.g:2483:1: rule__Edge__Group__2__Impl : ( 'from' ) ;
    public final void rule__Edge__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2487:1: ( ( 'from' ) )
            // InternalMediaFlow.g:2488:1: ( 'from' )
            {
            // InternalMediaFlow.g:2488:1: ( 'from' )
            // InternalMediaFlow.g:2489:2: 'from'
            {
             before(grammarAccess.getEdgeAccess().getFromKeyword_2()); 
            match(input,46,FOLLOW_2); 
             after(grammarAccess.getEdgeAccess().getFromKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__2__Impl"


    // $ANTLR start "rule__Edge__Group__3"
    // InternalMediaFlow.g:2498:1: rule__Edge__Group__3 : rule__Edge__Group__3__Impl rule__Edge__Group__4 ;
    public final void rule__Edge__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2502:1: ( rule__Edge__Group__3__Impl rule__Edge__Group__4 )
            // InternalMediaFlow.g:2503:2: rule__Edge__Group__3__Impl rule__Edge__Group__4
            {
            pushFollow(FOLLOW_32);
            rule__Edge__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Edge__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__3"


    // $ANTLR start "rule__Edge__Group__3__Impl"
    // InternalMediaFlow.g:2510:1: rule__Edge__Group__3__Impl : ( ( rule__Edge__SourceAssignment_3 ) ) ;
    public final void rule__Edge__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2514:1: ( ( ( rule__Edge__SourceAssignment_3 ) ) )
            // InternalMediaFlow.g:2515:1: ( ( rule__Edge__SourceAssignment_3 ) )
            {
            // InternalMediaFlow.g:2515:1: ( ( rule__Edge__SourceAssignment_3 ) )
            // InternalMediaFlow.g:2516:2: ( rule__Edge__SourceAssignment_3 )
            {
             before(grammarAccess.getEdgeAccess().getSourceAssignment_3()); 
            // InternalMediaFlow.g:2517:2: ( rule__Edge__SourceAssignment_3 )
            // InternalMediaFlow.g:2517:3: rule__Edge__SourceAssignment_3
            {
            pushFollow(FOLLOW_2);
            rule__Edge__SourceAssignment_3();

            state._fsp--;


            }

             after(grammarAccess.getEdgeAccess().getSourceAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__3__Impl"


    // $ANTLR start "rule__Edge__Group__4"
    // InternalMediaFlow.g:2525:1: rule__Edge__Group__4 : rule__Edge__Group__4__Impl rule__Edge__Group__5 ;
    public final void rule__Edge__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2529:1: ( rule__Edge__Group__4__Impl rule__Edge__Group__5 )
            // InternalMediaFlow.g:2530:2: rule__Edge__Group__4__Impl rule__Edge__Group__5
            {
            pushFollow(FOLLOW_3);
            rule__Edge__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Edge__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__4"


    // $ANTLR start "rule__Edge__Group__4__Impl"
    // InternalMediaFlow.g:2537:1: rule__Edge__Group__4__Impl : ( 'to' ) ;
    public final void rule__Edge__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2541:1: ( ( 'to' ) )
            // InternalMediaFlow.g:2542:1: ( 'to' )
            {
            // InternalMediaFlow.g:2542:1: ( 'to' )
            // InternalMediaFlow.g:2543:2: 'to'
            {
             before(grammarAccess.getEdgeAccess().getToKeyword_4()); 
            match(input,47,FOLLOW_2); 
             after(grammarAccess.getEdgeAccess().getToKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__4__Impl"


    // $ANTLR start "rule__Edge__Group__5"
    // InternalMediaFlow.g:2552:1: rule__Edge__Group__5 : rule__Edge__Group__5__Impl ;
    public final void rule__Edge__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2556:1: ( rule__Edge__Group__5__Impl )
            // InternalMediaFlow.g:2557:2: rule__Edge__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Edge__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__5"


    // $ANTLR start "rule__Edge__Group__5__Impl"
    // InternalMediaFlow.g:2563:1: rule__Edge__Group__5__Impl : ( ( rule__Edge__TargetAssignment_5 ) ) ;
    public final void rule__Edge__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2567:1: ( ( ( rule__Edge__TargetAssignment_5 ) ) )
            // InternalMediaFlow.g:2568:1: ( ( rule__Edge__TargetAssignment_5 ) )
            {
            // InternalMediaFlow.g:2568:1: ( ( rule__Edge__TargetAssignment_5 ) )
            // InternalMediaFlow.g:2569:2: ( rule__Edge__TargetAssignment_5 )
            {
             before(grammarAccess.getEdgeAccess().getTargetAssignment_5()); 
            // InternalMediaFlow.g:2570:2: ( rule__Edge__TargetAssignment_5 )
            // InternalMediaFlow.g:2570:3: rule__Edge__TargetAssignment_5
            {
            pushFollow(FOLLOW_2);
            rule__Edge__TargetAssignment_5();

            state._fsp--;


            }

             after(grammarAccess.getEdgeAccess().getTargetAssignment_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__Group__5__Impl"


    // $ANTLR start "rule__Graph__NameAssignment_1"
    // InternalMediaFlow.g:2579:1: rule__Graph__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Graph__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2583:1: ( ( RULE_ID ) )
            // InternalMediaFlow.g:2584:2: ( RULE_ID )
            {
            // InternalMediaFlow.g:2584:2: ( RULE_ID )
            // InternalMediaFlow.g:2585:3: RULE_ID
            {
             before(grammarAccess.getGraphAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getGraphAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__NameAssignment_1"


    // $ANTLR start "rule__Graph__NodesAssignment_3"
    // InternalMediaFlow.g:2594:1: rule__Graph__NodesAssignment_3 : ( ruleNode ) ;
    public final void rule__Graph__NodesAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2598:1: ( ( ruleNode ) )
            // InternalMediaFlow.g:2599:2: ( ruleNode )
            {
            // InternalMediaFlow.g:2599:2: ( ruleNode )
            // InternalMediaFlow.g:2600:3: ruleNode
            {
             before(grammarAccess.getGraphAccess().getNodesNodeParserRuleCall_3_0()); 
            pushFollow(FOLLOW_2);
            ruleNode();

            state._fsp--;

             after(grammarAccess.getGraphAccess().getNodesNodeParserRuleCall_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__NodesAssignment_3"


    // $ANTLR start "rule__Graph__EdgesAssignment_4"
    // InternalMediaFlow.g:2609:1: rule__Graph__EdgesAssignment_4 : ( ruleEdge ) ;
    public final void rule__Graph__EdgesAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2613:1: ( ( ruleEdge ) )
            // InternalMediaFlow.g:2614:2: ( ruleEdge )
            {
            // InternalMediaFlow.g:2614:2: ( ruleEdge )
            // InternalMediaFlow.g:2615:3: ruleEdge
            {
             before(grammarAccess.getGraphAccess().getEdgesEdgeParserRuleCall_4_0()); 
            pushFollow(FOLLOW_2);
            ruleEdge();

            state._fsp--;

             after(grammarAccess.getGraphAccess().getEdgesEdgeParserRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Graph__EdgesAssignment_4"


    // $ANTLR start "rule__Resource__NameAssignment_1"
    // InternalMediaFlow.g:2624:1: rule__Resource__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Resource__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2628:1: ( ( RULE_ID ) )
            // InternalMediaFlow.g:2629:2: ( RULE_ID )
            {
            // InternalMediaFlow.g:2629:2: ( RULE_ID )
            // InternalMediaFlow.g:2630:3: RULE_ID
            {
             before(grammarAccess.getResourceAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__NameAssignment_1"


    // $ANTLR start "rule__Resource__MediaTypeAssignment_5"
    // InternalMediaFlow.g:2639:1: rule__Resource__MediaTypeAssignment_5 : ( ruleMediaType ) ;
    public final void rule__Resource__MediaTypeAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2643:1: ( ( ruleMediaType ) )
            // InternalMediaFlow.g:2644:2: ( ruleMediaType )
            {
            // InternalMediaFlow.g:2644:2: ( ruleMediaType )
            // InternalMediaFlow.g:2645:3: ruleMediaType
            {
             before(grammarAccess.getResourceAccess().getMediaTypeMediaTypeEnumRuleCall_5_0()); 
            pushFollow(FOLLOW_2);
            ruleMediaType();

            state._fsp--;

             after(grammarAccess.getResourceAccess().getMediaTypeMediaTypeEnumRuleCall_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__MediaTypeAssignment_5"


    // $ANTLR start "rule__Resource__UriAssignment_9"
    // InternalMediaFlow.g:2654:1: rule__Resource__UriAssignment_9 : ( RULE_STRING ) ;
    public final void rule__Resource__UriAssignment_9() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2658:1: ( ( RULE_STRING ) )
            // InternalMediaFlow.g:2659:2: ( RULE_STRING )
            {
            // InternalMediaFlow.g:2659:2: ( RULE_STRING )
            // InternalMediaFlow.g:2660:3: RULE_STRING
            {
             before(grammarAccess.getResourceAccess().getUriSTRINGTerminalRuleCall_9_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getResourceAccess().getUriSTRINGTerminalRuleCall_9_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__UriAssignment_9"


    // $ANTLR start "rule__Resource__ExternalAssignment_12"
    // InternalMediaFlow.g:2669:1: rule__Resource__ExternalAssignment_12 : ( ruleEBoolean ) ;
    public final void rule__Resource__ExternalAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2673:1: ( ( ruleEBoolean ) )
            // InternalMediaFlow.g:2674:2: ( ruleEBoolean )
            {
            // InternalMediaFlow.g:2674:2: ( ruleEBoolean )
            // InternalMediaFlow.g:2675:3: ruleEBoolean
            {
             before(grammarAccess.getResourceAccess().getExternalEBooleanParserRuleCall_12_0()); 
            pushFollow(FOLLOW_2);
            ruleEBoolean();

            state._fsp--;

             after(grammarAccess.getResourceAccess().getExternalEBooleanParserRuleCall_12_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__ExternalAssignment_12"


    // $ANTLR start "rule__Resource__PortsAssignment_14"
    // InternalMediaFlow.g:2684:1: rule__Resource__PortsAssignment_14 : ( rulePort ) ;
    public final void rule__Resource__PortsAssignment_14() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2688:1: ( ( rulePort ) )
            // InternalMediaFlow.g:2689:2: ( rulePort )
            {
            // InternalMediaFlow.g:2689:2: ( rulePort )
            // InternalMediaFlow.g:2690:3: rulePort
            {
             before(grammarAccess.getResourceAccess().getPortsPortParserRuleCall_14_0()); 
            pushFollow(FOLLOW_2);
            rulePort();

            state._fsp--;

             after(grammarAccess.getResourceAccess().getPortsPortParserRuleCall_14_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Resource__PortsAssignment_14"


    // $ANTLR start "rule__Scaler__NameAssignment_1"
    // InternalMediaFlow.g:2699:1: rule__Scaler__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Scaler__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2703:1: ( ( RULE_ID ) )
            // InternalMediaFlow.g:2704:2: ( RULE_ID )
            {
            // InternalMediaFlow.g:2704:2: ( RULE_ID )
            // InternalMediaFlow.g:2705:3: RULE_ID
            {
             before(grammarAccess.getScalerAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__NameAssignment_1"


    // $ANTLR start "rule__Scaler__BackendAssignment_4"
    // InternalMediaFlow.g:2714:1: rule__Scaler__BackendAssignment_4 : ( ruleBackend ) ;
    public final void rule__Scaler__BackendAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2718:1: ( ( ruleBackend ) )
            // InternalMediaFlow.g:2719:2: ( ruleBackend )
            {
            // InternalMediaFlow.g:2719:2: ( ruleBackend )
            // InternalMediaFlow.g:2720:3: ruleBackend
            {
             before(grammarAccess.getScalerAccess().getBackendBackendEnumRuleCall_4_0()); 
            pushFollow(FOLLOW_2);
            ruleBackend();

            state._fsp--;

             after(grammarAccess.getScalerAccess().getBackendBackendEnumRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__BackendAssignment_4"


    // $ANTLR start "rule__Scaler__CommandAssignment_7"
    // InternalMediaFlow.g:2729:1: rule__Scaler__CommandAssignment_7 : ( RULE_STRING ) ;
    public final void rule__Scaler__CommandAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2733:1: ( ( RULE_STRING ) )
            // InternalMediaFlow.g:2734:2: ( RULE_STRING )
            {
            // InternalMediaFlow.g:2734:2: ( RULE_STRING )
            // InternalMediaFlow.g:2735:3: RULE_STRING
            {
             before(grammarAccess.getScalerAccess().getCommandSTRINGTerminalRuleCall_7_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getCommandSTRINGTerminalRuleCall_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__CommandAssignment_7"


    // $ANTLR start "rule__Scaler__ReplicasAssignment_10"
    // InternalMediaFlow.g:2744:1: rule__Scaler__ReplicasAssignment_10 : ( RULE_INT ) ;
    public final void rule__Scaler__ReplicasAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2748:1: ( ( RULE_INT ) )
            // InternalMediaFlow.g:2749:2: ( RULE_INT )
            {
            // InternalMediaFlow.g:2749:2: ( RULE_INT )
            // InternalMediaFlow.g:2750:3: RULE_INT
            {
             before(grammarAccess.getScalerAccess().getReplicasINTTerminalRuleCall_10_0()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getReplicasINTTerminalRuleCall_10_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__ReplicasAssignment_10"


    // $ANTLR start "rule__Scaler__WidthAssignment_13"
    // InternalMediaFlow.g:2759:1: rule__Scaler__WidthAssignment_13 : ( RULE_INT ) ;
    public final void rule__Scaler__WidthAssignment_13() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2763:1: ( ( RULE_INT ) )
            // InternalMediaFlow.g:2764:2: ( RULE_INT )
            {
            // InternalMediaFlow.g:2764:2: ( RULE_INT )
            // InternalMediaFlow.g:2765:3: RULE_INT
            {
             before(grammarAccess.getScalerAccess().getWidthINTTerminalRuleCall_13_0()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getWidthINTTerminalRuleCall_13_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__WidthAssignment_13"


    // $ANTLR start "rule__Scaler__HeightAssignment_16"
    // InternalMediaFlow.g:2774:1: rule__Scaler__HeightAssignment_16 : ( RULE_INT ) ;
    public final void rule__Scaler__HeightAssignment_16() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2778:1: ( ( RULE_INT ) )
            // InternalMediaFlow.g:2779:2: ( RULE_INT )
            {
            // InternalMediaFlow.g:2779:2: ( RULE_INT )
            // InternalMediaFlow.g:2780:3: RULE_INT
            {
             before(grammarAccess.getScalerAccess().getHeightINTTerminalRuleCall_16_0()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getScalerAccess().getHeightINTTerminalRuleCall_16_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__HeightAssignment_16"


    // $ANTLR start "rule__Scaler__PortsAssignment_18"
    // InternalMediaFlow.g:2789:1: rule__Scaler__PortsAssignment_18 : ( rulePort ) ;
    public final void rule__Scaler__PortsAssignment_18() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2793:1: ( ( rulePort ) )
            // InternalMediaFlow.g:2794:2: ( rulePort )
            {
            // InternalMediaFlow.g:2794:2: ( rulePort )
            // InternalMediaFlow.g:2795:3: rulePort
            {
             before(grammarAccess.getScalerAccess().getPortsPortParserRuleCall_18_0()); 
            pushFollow(FOLLOW_2);
            rulePort();

            state._fsp--;

             after(grammarAccess.getScalerAccess().getPortsPortParserRuleCall_18_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Scaler__PortsAssignment_18"


    // $ANTLR start "rule__Transcoder__NameAssignment_1"
    // InternalMediaFlow.g:2804:1: rule__Transcoder__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Transcoder__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2808:1: ( ( RULE_ID ) )
            // InternalMediaFlow.g:2809:2: ( RULE_ID )
            {
            // InternalMediaFlow.g:2809:2: ( RULE_ID )
            // InternalMediaFlow.g:2810:3: RULE_ID
            {
             before(grammarAccess.getTranscoderAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__NameAssignment_1"


    // $ANTLR start "rule__Transcoder__BackendAssignment_4"
    // InternalMediaFlow.g:2819:1: rule__Transcoder__BackendAssignment_4 : ( ruleBackend ) ;
    public final void rule__Transcoder__BackendAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2823:1: ( ( ruleBackend ) )
            // InternalMediaFlow.g:2824:2: ( ruleBackend )
            {
            // InternalMediaFlow.g:2824:2: ( ruleBackend )
            // InternalMediaFlow.g:2825:3: ruleBackend
            {
             before(grammarAccess.getTranscoderAccess().getBackendBackendEnumRuleCall_4_0()); 
            pushFollow(FOLLOW_2);
            ruleBackend();

            state._fsp--;

             after(grammarAccess.getTranscoderAccess().getBackendBackendEnumRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__BackendAssignment_4"


    // $ANTLR start "rule__Transcoder__CommandAssignment_7"
    // InternalMediaFlow.g:2834:1: rule__Transcoder__CommandAssignment_7 : ( RULE_STRING ) ;
    public final void rule__Transcoder__CommandAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2838:1: ( ( RULE_STRING ) )
            // InternalMediaFlow.g:2839:2: ( RULE_STRING )
            {
            // InternalMediaFlow.g:2839:2: ( RULE_STRING )
            // InternalMediaFlow.g:2840:3: RULE_STRING
            {
             before(grammarAccess.getTranscoderAccess().getCommandSTRINGTerminalRuleCall_7_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getCommandSTRINGTerminalRuleCall_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__CommandAssignment_7"


    // $ANTLR start "rule__Transcoder__ReplicasAssignment_10"
    // InternalMediaFlow.g:2849:1: rule__Transcoder__ReplicasAssignment_10 : ( RULE_INT ) ;
    public final void rule__Transcoder__ReplicasAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2853:1: ( ( RULE_INT ) )
            // InternalMediaFlow.g:2854:2: ( RULE_INT )
            {
            // InternalMediaFlow.g:2854:2: ( RULE_INT )
            // InternalMediaFlow.g:2855:3: RULE_INT
            {
             before(grammarAccess.getTranscoderAccess().getReplicasINTTerminalRuleCall_10_0()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getReplicasINTTerminalRuleCall_10_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__ReplicasAssignment_10"


    // $ANTLR start "rule__Transcoder__VideoCodecAssignment_13"
    // InternalMediaFlow.g:2864:1: rule__Transcoder__VideoCodecAssignment_13 : ( RULE_STRING ) ;
    public final void rule__Transcoder__VideoCodecAssignment_13() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2868:1: ( ( RULE_STRING ) )
            // InternalMediaFlow.g:2869:2: ( RULE_STRING )
            {
            // InternalMediaFlow.g:2869:2: ( RULE_STRING )
            // InternalMediaFlow.g:2870:3: RULE_STRING
            {
             before(grammarAccess.getTranscoderAccess().getVideoCodecSTRINGTerminalRuleCall_13_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getVideoCodecSTRINGTerminalRuleCall_13_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__VideoCodecAssignment_13"


    // $ANTLR start "rule__Transcoder__AudioCodecAssignment_16"
    // InternalMediaFlow.g:2879:1: rule__Transcoder__AudioCodecAssignment_16 : ( RULE_STRING ) ;
    public final void rule__Transcoder__AudioCodecAssignment_16() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2883:1: ( ( RULE_STRING ) )
            // InternalMediaFlow.g:2884:2: ( RULE_STRING )
            {
            // InternalMediaFlow.g:2884:2: ( RULE_STRING )
            // InternalMediaFlow.g:2885:3: RULE_STRING
            {
             before(grammarAccess.getTranscoderAccess().getAudioCodecSTRINGTerminalRuleCall_16_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getAudioCodecSTRINGTerminalRuleCall_16_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__AudioCodecAssignment_16"


    // $ANTLR start "rule__Transcoder__ContainerAssignment_19"
    // InternalMediaFlow.g:2894:1: rule__Transcoder__ContainerAssignment_19 : ( RULE_STRING ) ;
    public final void rule__Transcoder__ContainerAssignment_19() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2898:1: ( ( RULE_STRING ) )
            // InternalMediaFlow.g:2899:2: ( RULE_STRING )
            {
            // InternalMediaFlow.g:2899:2: ( RULE_STRING )
            // InternalMediaFlow.g:2900:3: RULE_STRING
            {
             before(grammarAccess.getTranscoderAccess().getContainerSTRINGTerminalRuleCall_19_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getContainerSTRINGTerminalRuleCall_19_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__ContainerAssignment_19"


    // $ANTLR start "rule__Transcoder__BitrateAssignment_22"
    // InternalMediaFlow.g:2909:1: rule__Transcoder__BitrateAssignment_22 : ( RULE_INT ) ;
    public final void rule__Transcoder__BitrateAssignment_22() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2913:1: ( ( RULE_INT ) )
            // InternalMediaFlow.g:2914:2: ( RULE_INT )
            {
            // InternalMediaFlow.g:2914:2: ( RULE_INT )
            // InternalMediaFlow.g:2915:3: RULE_INT
            {
             before(grammarAccess.getTranscoderAccess().getBitrateINTTerminalRuleCall_22_0()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getTranscoderAccess().getBitrateINTTerminalRuleCall_22_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__BitrateAssignment_22"


    // $ANTLR start "rule__Transcoder__PortsAssignment_24"
    // InternalMediaFlow.g:2924:1: rule__Transcoder__PortsAssignment_24 : ( rulePort ) ;
    public final void rule__Transcoder__PortsAssignment_24() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2928:1: ( ( rulePort ) )
            // InternalMediaFlow.g:2929:2: ( rulePort )
            {
            // InternalMediaFlow.g:2929:2: ( rulePort )
            // InternalMediaFlow.g:2930:3: rulePort
            {
             before(grammarAccess.getTranscoderAccess().getPortsPortParserRuleCall_24_0()); 
            pushFollow(FOLLOW_2);
            rulePort();

            state._fsp--;

             after(grammarAccess.getTranscoderAccess().getPortsPortParserRuleCall_24_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Transcoder__PortsAssignment_24"


    // $ANTLR start "rule__Port__NameAssignment_1"
    // InternalMediaFlow.g:2939:1: rule__Port__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Port__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2943:1: ( ( RULE_ID ) )
            // InternalMediaFlow.g:2944:2: ( RULE_ID )
            {
            // InternalMediaFlow.g:2944:2: ( RULE_ID )
            // InternalMediaFlow.g:2945:3: RULE_ID
            {
             before(grammarAccess.getPortAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getPortAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__NameAssignment_1"


    // $ANTLR start "rule__Port__DirectionAssignment_2"
    // InternalMediaFlow.g:2954:1: rule__Port__DirectionAssignment_2 : ( ruleDirection ) ;
    public final void rule__Port__DirectionAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2958:1: ( ( ruleDirection ) )
            // InternalMediaFlow.g:2959:2: ( ruleDirection )
            {
            // InternalMediaFlow.g:2959:2: ( ruleDirection )
            // InternalMediaFlow.g:2960:3: ruleDirection
            {
             before(grammarAccess.getPortAccess().getDirectionDirectionEnumRuleCall_2_0()); 
            pushFollow(FOLLOW_2);
            ruleDirection();

            state._fsp--;

             after(grammarAccess.getPortAccess().getDirectionDirectionEnumRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Port__DirectionAssignment_2"


    // $ANTLR start "rule__Edge__NameAssignment_1"
    // InternalMediaFlow.g:2969:1: rule__Edge__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Edge__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2973:1: ( ( RULE_ID ) )
            // InternalMediaFlow.g:2974:2: ( RULE_ID )
            {
            // InternalMediaFlow.g:2974:2: ( RULE_ID )
            // InternalMediaFlow.g:2975:3: RULE_ID
            {
             before(grammarAccess.getEdgeAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getEdgeAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__NameAssignment_1"


    // $ANTLR start "rule__Edge__SourceAssignment_3"
    // InternalMediaFlow.g:2984:1: rule__Edge__SourceAssignment_3 : ( ( RULE_ID ) ) ;
    public final void rule__Edge__SourceAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:2988:1: ( ( ( RULE_ID ) ) )
            // InternalMediaFlow.g:2989:2: ( ( RULE_ID ) )
            {
            // InternalMediaFlow.g:2989:2: ( ( RULE_ID ) )
            // InternalMediaFlow.g:2990:3: ( RULE_ID )
            {
             before(grammarAccess.getEdgeAccess().getSourcePortCrossReference_3_0()); 
            // InternalMediaFlow.g:2991:3: ( RULE_ID )
            // InternalMediaFlow.g:2992:4: RULE_ID
            {
             before(grammarAccess.getEdgeAccess().getSourcePortIDTerminalRuleCall_3_0_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getEdgeAccess().getSourcePortIDTerminalRuleCall_3_0_1()); 

            }

             after(grammarAccess.getEdgeAccess().getSourcePortCrossReference_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__SourceAssignment_3"


    // $ANTLR start "rule__Edge__TargetAssignment_5"
    // InternalMediaFlow.g:3003:1: rule__Edge__TargetAssignment_5 : ( ( RULE_ID ) ) ;
    public final void rule__Edge__TargetAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalMediaFlow.g:3007:1: ( ( ( RULE_ID ) ) )
            // InternalMediaFlow.g:3008:2: ( ( RULE_ID ) )
            {
            // InternalMediaFlow.g:3008:2: ( ( RULE_ID ) )
            // InternalMediaFlow.g:3009:3: ( RULE_ID )
            {
             before(grammarAccess.getEdgeAccess().getTargetPortCrossReference_5_0()); 
            // InternalMediaFlow.g:3010:3: ( RULE_ID )
            // InternalMediaFlow.g:3011:4: RULE_ID
            {
             before(grammarAccess.getEdgeAccess().getTargetPortIDTerminalRuleCall_5_0_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getEdgeAccess().getTargetPortIDTerminalRuleCall_5_0_1()); 

            }

             after(grammarAccess.getEdgeAccess().getTargetPortCrossReference_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Edge__TargetAssignment_5"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000208206000000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000008204000002L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x000000000003E000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000100002000000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x00000000001C0000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0000800000000000L});

}