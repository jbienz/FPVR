using System.Collections;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

/// <summary>
/// 360 slider component
/// </summary>
public class Slider360 : MonoBehaviour, IBeginDragHandler, IDragHandler, IEndDragHandler
{
    public GameObject thumbSign;
    public Image foreground;
    public Text label;
    public string format = "{0:00.00}";
    public bool percent = false;
    public float value = 0f;

    /// <summary>
    /// Start
    /// </summary>
    private void Start()
    {
        this.SetValue(this.value);
    }

    /// <summary>
    /// Callback when drag event began
    /// </summary>
    /// <param name="eventData"></param>
    public void OnBeginDrag(PointerEventData eventData)
    {
    }

    /// <summary>
    /// Mouse drag on slider.
    /// Calculate angle and set value
    /// </summary>
    /// <param name="eventData"></param>
    public void OnDrag(PointerEventData eventData)
    {
        Vector2 thumbPos = this.transform.position;
        Vector2 direction = thumbPos - eventData.position;
        direction.Normalize();
        float angle_Z = Vector3.Angle(direction, new Vector3(0, -1, 0));

        if (percent)
        {
            value = 100f * (360 - ((direction.x > 0) ? angle_Z : 360 - angle_Z)) / 360f;
            angle_Z = (direction.x > 0) ? angle_Z : -angle_Z;
            this.foreground.fillAmount = this.value / 100f;
        }
        else
        {
            value = (360 - ((direction.x > 0) ? angle_Z : 360 - angle_Z)) / 360f;
            angle_Z = (direction.x > 0) ? angle_Z : -angle_Z;
            this.foreground.fillAmount = this.value;
        }

        if (this.label != null) this.label.text = string.Format(this.format, this.value);
        this.thumbSign.transform.rotation = Quaternion.Euler(0, 0, angle_Z);
    }

    /// <summary>
    /// Callback when drag event end
    /// </summary>
    /// <param name="eventData"></param>
    public void OnEndDrag(PointerEventData eventData)
    {
    }

    /// <summary>
    /// Set value externaly.
    /// All others values like angle and value on slider is set too
    /// </summary>
    /// <param name="val"></param>
    public void SetValue(float val)
    {
        this.value = val;
        if (val > 1f) this.value = 1f;
        if (val < 0) this.value = 0f;

        this.foreground.fillAmount = this.value;
        float angle = this.value * 360f;
        this.thumbSign.transform.rotation = Quaternion.Euler(0, 0, angle);

        if (percent)
        {
            if (this.label != null) this.label.text = string.Format(this.format, this.value * 100);
        }
        else
        {
            if (this.label != null) this.label.text = string.Format(this.format, this.value);
        }
    }

    // Update is called once per frame
    private void Update()
    {
    }
}