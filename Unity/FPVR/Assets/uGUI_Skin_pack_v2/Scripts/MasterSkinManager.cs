using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class MasterSkinManager : MonoBehaviour
{
    public Color masterImageColor = Color.white;
    public Color masterTextColor = Color.black;
    public Color masterTextShadowColor = Color.gray;

    public bool realTimeUpdate = true;

    // Use this for initialization
    private void Start()
    {
        this.UpdateColors();
    }

    // Update is called once per frame
    private void Update()
    {
        if (this.realTimeUpdate) this.UpdateColors();
    }

    public void UpdateColors()
    {
        // IMAGES color
        Image[] images = GameObject.FindObjectsOfType<Image>();

        foreach (Image img in images)
        {
            if (img.tag != "IgnoreMasterColor")
            {
                Color actualColor = img.color;
                float alpha = actualColor.a;
                this.masterImageColor.a = alpha;
                img.color = this.masterImageColor;
            }
        }

        // TEXT color
        Text[] texts = GameObject.FindObjectsOfType<Text>();

        foreach (Text txt in texts)
        {
            if (txt.tag != "IgnoreMasterColor")
            {
                Color actualColor = txt.color;
                float alpha = actualColor.a;
                this.masterTextColor.a = alpha;
                txt.color = this.masterTextColor;
            }
        }

        // TEXT color
        Shadow[] shadows = GameObject.FindObjectsOfType<Shadow>();

        foreach (Shadow shd in shadows)
        {
            if (shd.tag != "IgnoreMasterColor")
            {
                Color actualColor = shd.effectColor;
                float alpha = actualColor.a;
                this.masterTextShadowColor.a = alpha;
                shd.effectColor = this.masterTextShadowColor;
            }
        }
    }
}