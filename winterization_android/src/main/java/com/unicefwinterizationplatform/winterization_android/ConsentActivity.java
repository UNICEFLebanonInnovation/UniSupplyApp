package com.unicefwinterizationplatform.winterization_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ConsentActivity extends HeaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        final PCodeObject pcodeObject = getIntent().getParcelableExtra("PCODE");
        final String barcodeType = getIntent().getStringExtra("barcodeType");
        //final String assistanceType = UserPrefs.getAssistanceType(getApplicationContext());
        final String assistanceType = getIntent().getStringExtra("ASSISTANCE");
        Button nextBtn = (Button)findViewById(R.id.consent_next);


        TextView tv1  = (TextView) findViewById(R.id.tv1);
        if (assistanceType.equals("cash")) {
            tv1.setText("\n" +
                    "موافقة: \n" +
                    "مرحبا, نحن ......................... ونعمل مع ..........................\n" +
                    "تقدم اليونيسف مساعدة نقديّة شتويّة للأطفال دون 15 عاماً  في المخيمات غير الرّسميّة والمساكن الجماعيّة. من أجل تقديم المساعدة بأفضل الظروف ووفقا للإحتياجات التي حدّدناها, نحن بحاجة لجمع معلومات أساسيّة عن أسرتك. لمنفعتك ولكي نقدّم المساعدة في الوقت المحدّد قد يتم مشاركة المعلومات التي  سوف تقدّمها لنا مع جمعيات ومنظمات إنسانيّة أخرى تقدّم مساعدات نقديّة. إنّ هذا طوعي ولك حرّية الإختيار  إذا ما كنت تريد أن تقدّم لنا المعلومات أم لا. هل تقبل الرّد على أسئلتنا؟\n" +
                    "\n" +
                    "نصائح للتّواصل:\n" +
                    "⎫\tقرر مكتب منظمة الأمم المتحدة للطفولة (اليونيسف) في لبنان الحفاظ على برنامج الإستجابة لحاجات فصل الشتاء 2016-2017 نتيجة الإحتياجات المتزايدة. وسوف يقوم البرنامج بإستهداف الفتيات والفتيان الأكثر عرضة للظروف المناخية الصعبة من خلال البرنامج الشتوي للمساعدة النقدية .\n" +
                    "⎫\tمن المخطط أن تغطي المساعدة النقدية بعضاً من المصاريف المنزلية المتعلقة بتوفير الإحتياجات الأساسية للأطفال مما يمكّنهم من العيش في ظروف مقبولة في فصل الشتاء البارد. وسيتم توفير المساعدة النقدية لولي أمر الطفل من خلال بطاقة لجهاز الصراف الآلي المصرفي. تتلقّى العائلة مساعدة لمرّة واحدة لكلّ طفل جرى تقييمه. \n" +
                    "\n" +
                    "⎫\tسيتمّ تحويل المبلغ إلى البطاقة الحمراء التي حصلتم أو ستحصلون عليها.\n" +
                    "\n" +
                    "\n" +
                    "⎫\tفي حال لم تحصل على البطاقة الحمراء، سيتمّ ابلاغك عبر رسائلة نّصيّة قصيرة للذّهاب إلى موقع توزيع محدّد وفي وقت محدّد لاستلام بطاقتك.\n" +
                    "\n" +
                    "⎫\tعندما يتمّ تعبئة البطاقة، سيتمّ إعلامك عن طريق رسالة نصّيّة مع المبلغ الذي حصلت عليه\n" +
                    "\n" +
                    "⎫\tبما انّنا سنتواصل معك من خلال الرسائل النّصّيّة، من الهم جدّا أن تزودنا برقم هاتف داخل الخدمة.\n");
        }
        else if (assistanceType.equals("kits"))
        {
            tv1.setText("\n" +"حالات الأطقم:\n" +
                    "\n" +
                    "موافقة:\n" +
                    "\n" +
                    "مرحبا, نحن ......................... ونعمل مع ..........................\n" +
                    "\n" +
                    "تقدم اليونيسف مساعدة شتويّة عبارة عن مجموعة من الملابس الشتوية للأطفال دون ١٥ عاماً في المخيمات غير الرّسميّة. من أجل تقديم المساعدة بأفضل الظروف ووفقا للإحتياجات التي حدّدناها، نحن بحاجة لجمع معلومات أساسيّة عن أسرتك. لمنفعتك ولكي نقدّم المساعدة في الوقت المحدّد، قد يتم مشاركة المعلومات التي سوف تقدّمها لنا مع جمعيات ومنظمات إنسانيّة أخرى تقدّم مساعدات نقديّة. إنّ هذا طوعي ولك حرّية الإختيار إذا ما كنت تريد أن تقدّم لنا المعلومات أم لا. هل تقبل الرّد على أسئلتنا؟\n" +
                    "\n" +
                    "نصائح للتّواصل:\n" +
                    "\n" +
                    "ü قرر مكتب منظمة الأمم المتحدة للطفولة (اليونيسف) في لبنان الحفاظ على برنامج الإستجابة لحاجات فصل الشتاء ٢٠١٧-٢٠١٨ نتيجة الإحتياجات المتزايدة مستهدفة كل الأطفال السّوريين الذين تتراوح أعمارهم بين ٠ و ١٥ سنة وعائلاتهم, الذين يعيشون في مستوطنات غير رسميّة وملاجئ جماعيّة في الأماكن الصّعب الوصول إليها.\n" +
                    "\n" +
                    "ü سيحصل كلّ طفل جرى تقييمه على مجموعة من الملابس الشتوية مناسبة لعمره.\n" +
                    "\n" +
                    "ü إنّ توزيع الملابس الشتوية سيتمّ في مكان ووقت محدّد. سيتمّ ابلاغكم عن المكان والتّوقيت من خلال رسالة نصّيّة على رقم الهاتف الذي زوّدتنا به.\n" +
                    "\n" +
                    "ü لدى انتهائنا من الأسئلة سنعطيك بطاقة مع رمز. ستكون البطاقة هذه وثيقة التّعريف التي ستخوّلك الدّخول إلى موقع التّوزيع واستلام الملابس الممنوحة لك. لذا، يرجى الحفاظ على البطاقة بمكان آمن وإحضارها إلى موقع التّوزيع عندما نتواصل معك عبر رسالة نصّيّة.\n" +
                    "\n" +
                    "ü بما انّنا سنتواصل معك من خلال الرسائل النّصّيّة، من الهم جدّا أن تزودنا برقم هاتف داخل الخدمة.");

        }


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (assistanceType.equals("cash")) {
                    Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
                    intent.putExtra("PCODE", pcodeObject);
                    intent.putExtra("barcodeType", barcodeType);
                    intent.putExtra("ASSISTANCE",assistanceType);
                    intent.setAction(getIntent().getAction());
                    startActivity(intent);
                }
                else if (assistanceType.equals("kits"))
                {
                    Intent intent = new Intent(getApplicationContext(), AddBeneficiaryActivity.class);
                    intent.putExtra("PCODE", pcodeObject);
                    BeneficiaryObject beneficiaryObject = new BeneficiaryObject();
                    beneficiaryObject.setPcode(pcodeObject);
                    intent.putExtra("BENEFICIARY",beneficiaryObject);
                    intent.putExtra("barcodeType", barcodeType);
                    intent.putExtra("ASSISTANCE",assistanceType);
                    intent.setAction(getIntent().getAction());
                    startActivity(intent);
                }
            }
        });





    }

}
