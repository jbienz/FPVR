using FPVR;
using SolerSoft.Plugins;
using SolerSoft.UI;
using System.Collections;
using System.Collections.ObjectModel;
using System.IO;
using UnityEngine;
using UnityEngine.UI;

public class PluginManager : MonoBehaviour {

	// Inspector Items
    public string appName;                  // The "friendly" name of the app. Used when creating a directory for the game under AppData.
    public Selector providerSelector;       // The selector control used to pick a vehicle provider.
    public Selector vehicleSelector;        // The selector control used to pick a vehicle.
    public bool searchApp = true;           // true to search for plugins compiled into the game itself
    public string searchPath;               // Custom directory path to search for plugins. null = disabled (default)
    public bool searchLocalAppData = true;  // true to search for plugins in c:\Users\[User]\AppData\Local\[AppName]\Plugins

	// Member Variables
	private PluginCatalog catalog;
	private PluginQueryResult pluginResults;
    private IVehicleProvider vehicleProvider;
    private ObservableCollection<PluginInfo> vehicleProviders;
    private ObservableCollection<VehicleInfo> vehicles;
    private IVehicle vehicle;
    private VehicleManager vehicleManager;

	private void ActivatePlugin(PluginInfo info)
	{
		// Create the plugin instance
		vehicleProvider = (IVehicleProvider)catalog.CreateInstance(info);
        vehicles = new ObservableCollection<VehicleInfo>(vehicleProvider.GetVehicles());
        vehicleSelector.ItemsSource = vehicles;
	}

	private string GetLocalPluginPath()
	{
		// Make sure we have an app name
		if (string.IsNullOrEmpty(appName)) { throw new UnityException("App Name cannot be blank when using Search Local App Data option"); }

		// Get path to Local Data
		var path = System.Environment.GetFolderPath(System.Environment.SpecialFolder.LocalApplicationData);

		// Add the app name to path
		path = Path.Combine(path, appName);

		// Make sure the app folder exists
		if (!Directory.Exists(path))
		{
			Directory.CreateDirectory(path);
		}

		// Add plugins folder name to path
		path = Path.Combine(path, "Plugins");

		// Make sure the plugin folder exists
		if (!Directory.Exists(path))
		{
			Directory.CreateDirectory(path);
		}

		// Done
		return path;
	}

	private void FindPlugins()
	{
		// Create the catalog
		catalog = new PluginCatalog();

		// Search the app itself?
		if (searchApp)
		{
			catalog.Sources.Add(new AssemblyPluginSource(this.GetType().Assembly));
		}
		
		// Was a custom search path specified?
		if (!string.IsNullOrEmpty(searchPath))
		{
			catalog.Sources.Add(new DirectoryPluginSource(searchPath));
		}

        // Are we searching local app data?
		if (searchLocalAppData)
		{
			catalog.Sources.Add(new DirectoryPluginSource(GetLocalPluginPath()));
		}
		
		// If there are no sources just bail because there's nothing to do
		if (catalog.Sources.Count < 1) { return; }
		
		// Create the plugin query
		var query = new PluginQuery();

		// We're only looking for one plugin interface
		query.Interfaces.Add(typeof(IVehicleProvider));

		// Search!
		pluginResults = catalog.ExecuteQuery(query);
	}

	private void ShowPlugins()
	{
		// If there are no plugins just bail
		if (pluginResults == null) { return; }

		// Only care about vehicle providers
        if (pluginResults.Interfaces.ContainsKey(typeof(IVehicleProvider)))
        {
            vehicleProviders = new ObservableCollection<PluginInfo>(pluginResults.Interfaces[typeof(IVehicleProvider)]);
            providerSelector.ItemsSource = vehicleProviders;
        }
    }

	// Use this for initialization
	void Start ()
	{
        // Get other services
        vehicleManager = GetComponentInParent<VehicleManager>();

        // Find and show plugins
		FindPlugins();
		ShowPlugins();

        // Subscribe to events
        providerSelector.SelectionChanged += providerSelector_SelectionChanged;
	}

    private void providerSelector_SelectionChanged(object sender, SelectionChangedEventArgs e)
    {
        // Get as plugin info
        var pi = providerSelector.SelectedItem as PluginInfo;

        // If no selection, ignore
        if (pi == null) { return; }

        // Create the vhicle provider
        vehicleProvider = (IVehicleProvider)catalog.CreateInstance(pi);

        // Get info for all vehicles
        vehicles = new ObservableCollection<VehicleInfo>(vehicleProvider.GetVehicles());

        // If not at least one vehicle, bail
        if ((vehicles == null) || (vehicles.Count < 1)) { return; }

        // Use first vehicle
        vehicle = vehicleProvider.GetVehicle(vehicles[0]);

        // Update vehicle manager
        vehicleManager.vehicle = vehicle;
    }
}
